package net.fabricmc.renew_auto_plus;

import java.util.HashMap;
import java.util.Vector;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AbacusBlockEntity extends LockableContainerBlockEntity implements ExtendedScreenHandlerFactory {
    private final ItemStack EMERALD_COMP_STACK = Items.EMERALD.getDefaultStack();

    private DefaultedList<ItemStack> inventory;
    private int emeraldAmount;
    private int emeraldChange;
    private int currentAutoTradeIndex = 0;
    private boolean needsAttachedCrates = false;

    public String companyName = "";
    public HashMap<BlockPos, CrateBlockEntity> attachedCrates = new HashMap<>();
    public StallBlockEntity attachedMarket;
    public DefaultedList<String> ownerNameList;
    public Vector<AutoStallTrade> autoTradeList = new Vector<>();

    public final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0: {
                    return AbacusBlockEntity.this.emeraldAmount;
                }
                case 1: {
                    return AbacusBlockEntity.this.emeraldChange;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    AbacusBlockEntity.this.setEmeraldAmount(value);
                    break;
                }
                case 1: {
                    AbacusBlockEntity.this.setEmeraldChange(value);
                    break;
                }
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };

    public AbacusBlockEntity(BlockPos pos, BlockState state) {
        super(RenewAutoPlusInitialize.ABACUS_BLOCK_ENTITY, pos, state);
        this.inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
        this.ownerNameList = DefaultedList.of();
        emeraldAmount = 0;
        emeraldChange = 0;
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
        this.emeraldAmount = nbt.getShort("EmeraldAmount");
        this.emeraldChange = nbt.getShort("EmeraldChange");
        this.companyName = nbt.getString("CompanyName");
        needsAttachedCrates = true;
        NbtList cratePosList = nbt.getList("AttachedCrates", NbtCompound.COMPOUND_TYPE);
        if(cratePosList == null){
            return;
        }
        for(NbtElement element : cratePosList) {
            if(element instanceof NbtCompound) {
                NbtCompound posCompound = (NbtCompound)element;
                int x = posCompound.getInt("X");
                int y = posCompound.getInt("Y");
                int z = posCompound.getInt("Z");
                attachedCrates.put(new BlockPos(x, y, z), null);
            }
        }
     }
  
     public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putShort("EmeraldAmount", (short)this.emeraldAmount);
        nbt.putShort("EmeraldChange", (short)this.emeraldChange);
        nbt.putString("CompanyName", companyName);
        NbtList cratePosList = new NbtList();
        for(BlockPos pos : attachedCrates.keySet()) {
            NbtCompound posCompound = new NbtCompound();
            posCompound.putInt("X", pos.getX());
            posCompound.putInt("Y", pos.getY());
            posCompound.putInt("Z", pos.getZ());
            cratePosList.add(posCompound);
        }
        if(!cratePosList.isEmpty()) {
            nbt.put("AttachedCrates", cratePosList);
        }
     }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if(!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return removeStack(slot, 1);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = this.inventory.get(slot);
        boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(itemStack) && ItemStack.areNbtEqual(stack, itemStack);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        if (slot == 0 && !bl) {
            this.markDirty();
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        for(int i = 0; i < inventory.size(); ++i) {
            inventory.set(i, ItemStack.EMPTY);
        }
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new AbacusScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public void updateCompanyName(String newName) {
        if(newName == companyName || MarketManager.instance().allCompanies.containsKey(newName)) { //Maybe handle name existing with return packet
            return;
        }
        MarketManager.instance().removeCompany(companyName);
        MarketManager.instance().addCompany(newName, this);
        this.companyName = newName;
    }

    public void tryAddOwner(String newName) {
        if(ownerNameList.size() < MarketManager.MAX_ABACUS_OWNERS) {
            ownerNameList.add(newName);
        }
    }

    public void removeOwner(String name) {
        int i = 0;
        for(String owner : ownerNameList) {
            if(owner.equals(name)) {
                ownerNameList.remove(i);
                break;
            }
            ++i;
        }
    }

    public void removeAutoTrade(int index) {
        if(index >= autoTradeList.size() || index < 0) return;
        autoTradeList.remove(index);
    }

    public int getOwnerListSize() {
        return ownerNameList.size();
    }

    public void tryToAttachToMarket() {
        if(attachedMarket != null){
            return;
        }
        StallBlockEntity currentClosest = null;
        int closestDistance = MarketManager.MAX_MARKET_DISTANCE;
        if(MarketManager.instance().allMarkets.isEmpty()) return;
        for (StallBlockEntity market : MarketManager.instance().allMarkets) {
            if(this.getPos().isWithinDistance(market.getPos(), closestDistance)){
                currentClosest = market;
                closestDistance = (int)this.getPos().getSquaredDistance(currentClosest.getPos(), true);
            }
        }
        if(currentClosest != null) {
            this.attachedMarket = currentClosest;
        }
    }

    public void attachCrate(CrateBlockEntity crate) {
        if(attachedCrates.size() < MarketManager.MAX_ATTACHED_CRATES) {
            attachedCrates.put(crate.getPos(), crate);
        }
    }

    public void attachCrate(BlockPos pos) {
        if(!(attachedCrates.size() < MarketManager.MAX_ATTACHED_CRATES)) return;
        CrateBlockEntity crate = this.world.getBlockEntity(pos, RenewAutoPlusInitialize.CRATE_BLOCK_ENTITY).orElse(null);
        if(crate != null) {
            attachCrate(crate);
        }
    }

    public void removeCrate(BlockPos pos) {
        attachedCrates.remove(pos);
    }

    public void handleTransactPacket(StallTrade stallTrade, Boolean isPurchase, int tradeAmount) {
        getRealEmeraldAmount();
        if(stallTrade.isAutoTradeEnabled())
        {
            if(tradeAmount > 0){
                if(autoTradeList.size() < MarketManager.MAX_AUTO_TRADES){
                    autoTradeList.add(new AutoStallTrade(stallTrade, tradeAmount, isPurchase));
                }
            }
        }
        else
        {
            if(isPurchase) { //Might need to factor in going over stock states
                addItemToStackOrEmpty(stallTrade.getTradedItem(), tradeAmount);
                attachedMarket.removeItemOrMaterialsFromInventory(stallTrade.getTradedItem().getItem(), tradeAmount);
                decreaseRealEmeralds((stallTrade.getBuyEmeraldAmount() * tradeAmount) + ((100 - this.emeraldChange) + stallTrade.getBuyEmeraldChange() * tradeAmount) / 100);
                decreaseEmeraldChange(stallTrade.getBuyEmeraldChange() * tradeAmount);
            }
            else {
                removeItemInStackOrEmpty(stallTrade.getTradedItem(), tradeAmount);
                attachedMarket.addItemToInventory(stallTrade.getTradedItem().getItem(), tradeAmount);
                increaseRealEmeralds((stallTrade.getSellEmeraldAmount() * tradeAmount) + (this.emeraldChange + stallTrade.getSellEmeraldChange() * tradeAmount) / 100);
                increaseEmeraldChange(stallTrade.getSellEmeraldChange() * tradeAmount);
            }
        }
        return;
    }

    private int checkIfHaveToSell(StallTrade trade) {
        int count = 0;
        for (ItemStack stack : inventory) {
            if(!stack.isEmpty()){
                if(ItemStack.areItemsEqual(stack, trade.getTradedItem())) {
                    count += stack.getCount();
                }
            }
        }
        for (CrateBlockEntity crate : this.attachedCrates.values()) {
            for (ItemStack stack : crate.getInventory()) {
                if(!stack.isEmpty()){
                    if(ItemStack.areItemsEqual(stack, trade.getTradedItem())) {
                        count += stack.getCount();
                    }
                }
            }
        }
        return count;
    }

    public void setEmeraldChange(int amount) {
        if(amount == this.emeraldChange) {
            return;
        }

        if(amount > this.emeraldChange) {
            if(amount >= 100) {
                //increaseRealEmeralds(amount / 100);
                this.emeraldChange = amount % 100;
            }
            else {
                this.emeraldChange = amount;
            }
        }
        else {
            if(amount < 0) {
                //decreaseRealEmeralds(Math.abs(amount) / 100);
                this.emeraldChange = 100 - (Math.abs(amount) % 100);
            }
            else {
                this.emeraldChange = amount;
            }
        }
    }

    private void increaseEmeraldChange(int amount) {
        setEmeraldChange(emeraldChange + amount);
    }

    private void decreaseEmeraldChange(int amount) {
        setEmeraldChange(emeraldChange - amount);
    }

    public void setEmeraldAmount(int amount) {
        if(amount == this.emeraldAmount) {
            return;
        }

        if(amount > this.emeraldAmount) {
            this.increaseRealEmeralds(amount - this.emeraldAmount);
        }
        else {
            this.decreaseRealEmeralds(this.emeraldAmount - amount);
        }
    }

    private void increaseRealEmeralds(int amount) {
        if(amount <= 0) {
            return;
        }
        for (ItemStack stack : inventory) {
            if(ItemStack.areItemsEqual(stack, EMERALD_COMP_STACK)) {
                if(!stack.isEmpty()){
                    if(stack.getCount() + amount <= stack.getMaxCount()) {
                        stack.increment(amount);
                        this.emeraldAmount += amount; //Keeps amount accurate without needing re-sync
                        return;
                    }
                    else {
                        int difference = stack.getMaxCount() - stack.getCount();
                        stack.increment(difference);
                        this.emeraldAmount += difference;
                        amount -= difference;
                    }
                }
            }
        }
        for (CrateBlockEntity crate : this.attachedCrates.values()) {
            for (ItemStack stack : crate.getInventory()) {
                if(!stack.isEmpty()){
                    if(ItemStack.areItemsEqual(stack, EMERALD_COMP_STACK)) {
                        if(stack.getCount() + amount <= stack.getMaxCount()) {
                            stack.increment(amount);
                            this.emeraldAmount += amount;
                            return;
                        }
                        else {
                            int difference = stack.getMaxCount() - stack.getCount();
                            stack.increment(difference);
                            this.emeraldAmount += difference;
                            amount -= difference;
                        }
                    }
                }
            }
        }

        //Add new stack if no room
        int i = 0;
        for (ItemStack stack : inventory) {
            if(stack.isEmpty()) {
                if(amount <= 64) {
                    this.setStack(i, new ItemStack(Items.EMERALD, amount));
                    return;
                }
                else {
                    this.setStack(i, new ItemStack(Items.EMERALD, 64));
                    amount -= 64;
                }
            }
            i++;
        }
        for (CrateBlockEntity crate : this.attachedCrates.values()) {
            i = 0;
            for (ItemStack stack : crate.getInventory()) {
                if(stack.isEmpty()) {
                    if(amount <= 64) {
                        crate.setStack(i, new ItemStack(Items.EMERALD, amount));
                        return;
                    }
                    else {
                        crate.setStack(i, new ItemStack(Items.EMERALD, 64));
                        amount -= 64;
                    }
                }
                i++;
            }
        }
    }

    private void decreaseRealEmeralds(int amount) {
        if(amount <= 0) {
            return;
        }
        for (ItemStack stack : inventory) {
            if(!stack.isEmpty()){
                if(ItemStack.areItemsEqual(stack, EMERALD_COMP_STACK)) {
                    if(stack.getCount() - amount >= 0) {
                        stack.decrement(amount);
                        this.emeraldAmount -= amount;
                        return;
                    }
                    else {
                        amount -= stack.getCount();
                        this.emeraldAmount -= stack.getCount();
                        stack.decrement(stack.getCount());
                    }

                }
            }
        }
        for (CrateBlockEntity crate : this.attachedCrates.values()) {
            for (ItemStack stack : crate.getInventory()) {
                if(!stack.isEmpty()){
                    if(ItemStack.areItemsEqual(stack, EMERALD_COMP_STACK)) {
                        if(stack.getCount() - amount >= 0) {
                            stack.decrement(amount);
                            this.emeraldAmount -= amount;
                            return;
                        }
                        else {
                            amount -= stack.getCount();
                            this.emeraldAmount -= stack.getCount();
                            stack.decrement(stack.getCount());
                        }
                    }
                }
            }
        }
    }

    private void getRealEmeraldAmount() {
        int newAmount = 0;
        for (ItemStack stack : inventory) {
            if(!stack.isEmpty()){
                if(ItemStack.areItemsEqual(stack, EMERALD_COMP_STACK)) {
                    newAmount += stack.getCount();
                }
            }
        }
        for (CrateBlockEntity crate : this.attachedCrates.values()) {
            for (ItemStack stack : crate.getInventory()) {
                if(!stack.isEmpty()){
                    if(ItemStack.areItemsEqual(stack, EMERALD_COMP_STACK)) {
                        newAmount += stack.getCount();
                    }
                }
            }
        }
        emeraldAmount = newAmount;
    }

    private void addItemToStackOrEmpty(ItemStack addItem, int amount) {
        if(amount <= 0) {
            return;
        }
        for (ItemStack stack : inventory) {
            if(!stack.isEmpty()) {
                if(ItemStack.areItemsEqual(stack, addItem)) {
                    if(stack.getCount() + amount <= stack.getMaxCount()) {
                        stack.increment(amount);
                        return;
                    }
                    else {
                        int difference = stack.getMaxCount() - stack.getCount();
                        stack.increment(difference);
                        amount -= difference;
                    }
                }
            }
        }
        for (CrateBlockEntity crate : this.attachedCrates.values()) {
            for (ItemStack stack : crate.getInventory()) {
                if(!stack.isEmpty()){
                    if(ItemStack.areItemsEqual(stack, addItem)) {
                        if(stack.getCount() + amount <= stack.getMaxCount()) {
                            stack.increment(amount);
                            return;
                        }
                        else {
                            int difference = stack.getMaxCount() - stack.getCount();
                            stack.increment(difference);
                            amount -= difference;
                        }
                    }
                }
            }
        }

        int i = 0;
        for (ItemStack stack : inventory) {
            if(stack.isEmpty()) {
                if(amount <= 64) {
                    this.setStack(i, new ItemStack(addItem.getItem(), amount));
                    return;
                }
                else {
                    this.setStack(i, new ItemStack(addItem.getItem(), 64));
                    amount -= 64;
                }
            }
            i++;
        }
        for (CrateBlockEntity crate : this.attachedCrates.values()) {
            i = 0;
            for (ItemStack stack : crate.getInventory()) {
                if(stack.isEmpty()) {
                    if(amount <= 64) {
                        crate.setStack(i, new ItemStack(addItem.getItem(), amount));
                        return;
                    }
                    else {
                        crate.setStack(i, new ItemStack(addItem.getItem(), 64));
                        amount -= 64;
                    }
                }
                i++;
            }
        }
    }

    private void removeItemInStackOrEmpty(ItemStack addItem, int amount) {
        if(amount <= 0) {
            return;
        }
        int i = 0;
        for (ItemStack stack : inventory) {
            if(!stack.isEmpty()){
                if(ItemStack.areItemsEqual(stack, addItem)) {
                    if(stack.getCount() - amount >= 0) {
                        stack.decrement(amount);
                        return;
                    }
                    else {
                        amount -= stack.getCount();
                        this.setStack(i, ItemStack.EMPTY);
                    }

                }
            }
            i++;
        }
        for (CrateBlockEntity crate : this.attachedCrates.values()) {
            i = 0;
            for (ItemStack stack : crate.getInventory()) {
                if(!stack.isEmpty()){
                    if(ItemStack.areItemsEqual(stack, addItem)) {
                        if(stack.getCount() - amount >= 0) {
                            stack.decrement(amount);
                            return;
                        }
                        else {
                            amount -= stack.getCount();
                            this.setStack(i, ItemStack.EMPTY);
                        }
                    }
                }
                i++;
            }
        }
    }

    public static boolean canBuyWithAmount(int emeraldAmount, int emeraldChange, int tradeAmount, int ownedEmeralds, int ownedChange)
    {
        if(emeraldChange == 0){
            emeraldAmount = emeraldAmount * tradeAmount;
        }
        else {
            emeraldChange = emeraldChange * tradeAmount;
            emeraldAmount += emeraldChange / 100;
            emeraldChange = emeraldChange % 100;
        }
        if(ownedEmeralds > emeraldAmount) {
            return true;
        }
        else if(ownedChange >= emeraldChange && ownedEmeralds >= emeraldAmount) {
            return true;
        }
        return false;
    }

    public int getAutoTradeIndex() {
        return currentAutoTradeIndex;
    }

    public void iterateAutoTradeIndex() {
        if(autoTradeList.size() <= 0) return;
        currentAutoTradeIndex = (currentAutoTradeIndex + 1) % autoTradeList.size(); 
    }

    // Passing in index incase somewhere else wants to use it
    public void tryTransactAutoTrade(int index) {
        if(index < 0 || index >= autoTradeList.size()) return;
        if(attachedMarket == null) return;

        AutoStallTrade trade = autoTradeList.get(index);
        getRealEmeraldAmount();
        if(trade.isPurchase) {
            if(attachedMarket.getItemsForTrade(trade, trade.tradeAmount) > 0 && canBuyWithAmount(trade.getBuyEmeraldAmount(), trade.getBuyEmeraldChange(), trade.tradeAmount, this.emeraldAmount, this.emeraldChange)) {
                addItemToStackOrEmpty(trade.getTradedItem(), trade.tradeAmount);
                attachedMarket.removeItemOrMaterialsFromInventory(trade.getTradedItem().getItem(), trade.tradeAmount);
                decreaseRealEmeralds((trade.getBuyEmeraldAmount() * trade.tradeAmount) + (trade.getBuyEmeraldChange() * trade.tradeAmount) / 100);
                decreaseEmeraldChange(trade.getBuyEmeraldChange() * trade.tradeAmount);
            }
        }
        else {
            if(checkIfHaveToSell(trade) >= trade.tradeAmount) {
                removeItemInStackOrEmpty(trade.getTradedItem(), trade.tradeAmount);
                attachedMarket.addItemToInventory(trade.getTradedItem().getItem(), trade.tradeAmount);
                increaseRealEmeralds((trade.getSellEmeraldAmount() * trade.tradeAmount) + (trade.getSellEmeraldChange() * trade.tradeAmount) / 100);
                increaseEmeraldChange(trade.getSellEmeraldChange() * trade.tradeAmount);
            }
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, AbacusBlockEntity blockEntity) {
        //blockEntity.tryTransactAutoTrade(blockEntity.getAutoTradeIndex());
        //blockEntity.iterateAutoTradeIndex();
        return;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);
        packetByteBuf.writeString(companyName);
        if(needsAttachedCrates) {
            for(BlockPos pos : attachedCrates.keySet()) {
                attachCrate(pos);
            }
            needsAttachedCrates = false;
        }
        tryToAttachToMarket();
        packetByteBuf.writeBoolean(attachedMarket != null);
        if(attachedMarket != null) {
            getRealEmeraldAmount();
            StallTradeList tradeList = attachedMarket.getStallTradeList();
            for(StallTrade stallTrade : tradeList) {
                int i = checkIfHaveToSell(stallTrade);
                if(i > 0){
                    stallTrade.setSellable(true);
                    stallTrade.setCompanyItemAmount(i);
                }
                else {
                    stallTrade.setSellable(false);
                    stallTrade.setCompanyItemAmount(0);
                }
            }
            tradeList.toPacket(packetByteBuf);
        }
        packetByteBuf.writeByte((byte)(ownerNameList.size() & 0xFF));
        for(String name : ownerNameList) {
            packetByteBuf.writeString(name);
        }

        packetByteBuf.writeByte((byte)(attachedCrates.size() & 0xFF));
        for(BlockPos pos : attachedCrates.keySet()) {
            packetByteBuf.writeBlockPos(pos);
        }

        packetByteBuf.writeByte((byte)(autoTradeList.size() & 0xFF));
        boolean isActive = true;
        for(AutoStallTrade trade : autoTradeList) {
            isActive = trade.isActive();
            trade.setActive(trade.isPurchase);
            trade.toPacket(packetByteBuf);
            trade.setActive(isActive);
        }
    }

    private class AutoStallTrade extends StallTrade {
        public int tradeAmount = 1;
        public boolean isPurchase = false;

        AutoStallTrade(StallTrade trade, int amount, boolean isPurchase) {
            super(trade.getTradedItem(), trade.getEmeraldAmount(), trade.getEmeraldChange(), trade.getAmountBeforeCommon());
            this.tradeAmount = amount;
            this.isPurchase = isPurchase;
        }
    }
}

