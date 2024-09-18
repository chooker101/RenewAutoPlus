package net.fabricmc.renew_auto_plus;

import com.mojang.datafixers.util.Pair;

import net.fabricmc.renew_auto_plus.helper.OverworldInventoryStorage;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class WastesPortalControllerScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PlayerInventory playerInventory;
    private final PropertyDelegate propertyDelegate;
    private final Inventory storedInventory;
    public static final Identifier BLOCK_ATLAS_TEXTURE = new Identifier("textures/atlas/blocks.png");
    public static final Identifier EMPTY_HELMET_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_helmet");
    public static final Identifier EMPTY_CHESTPLATE_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_chestplate");
    public static final Identifier EMPTY_LEGGINGS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_leggings");
    public static final Identifier EMPTY_BOOTS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_boots");
    public static final Identifier EMPTY_OFFHAND_ARMOR_SLOT = new Identifier("item/empty_armor_slot_shield");
    static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[]{EMPTY_BOOTS_SLOT_TEXTURE, EMPTY_LEGGINGS_SLOT_TEXTURE, EMPTY_CHESTPLATE_SLOT_TEXTURE, EMPTY_HELMET_SLOT_TEXTURE};
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public WastesPortalControllerScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(1), new SimpleInventory(41),  new ArrayPropertyDelegate(1));
    }
 
    public WastesPortalControllerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, Inventory storedInventory, PropertyDelegate propertyDelegate) {
        super(RenewAutoPlusInitialize.WASTES_PORTAL_SCREEN_HANDLER, syncId);
        checkSize(inventory, 1);
        checkDataCount(propertyDelegate, 1);
        this.propertyDelegate = propertyDelegate;
        this.inventory = inventory;
        this.playerInventory = playerInventory;
        if(storedInventory == null) {
            this.storedInventory = new SimpleInventory(41);
        }
        else {
            this.storedInventory = storedInventory;
        }
        addPortalSlots();
        this.addProperties(this.propertyDelegate);
    }
 
    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @SuppressWarnings("resource")
    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if(id != 0) {
            return false;
        }
        player.method_48926().playSound(null, player.getBlockPos(), SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.BLOCKS, 1.0f, player.method_48926().random.nextFloat() * 0.1f + 0.9f);
        return true;
    }
 
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if(invSlot < 36 && itemStack2.isOf(Items.DIAMOND)) {
                if(!this.insertItem(itemStack2, 82, 83, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if(invSlot < 27) {
                if(!this.insertItem(itemStack2, 27, 36, true)) {
                    return ItemStack.EMPTY;
                }
            }
            if(invSlot >= 27 && invSlot < 36) {
                if(!this.insertItem(itemStack2, 0, 27, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if(invSlot >= 27 && invSlot < 36) {
                if(!this.insertItem(itemStack2, 0, 27, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if(invSlot >= 36 && invSlot < 41) {
                if(!this.insertItem(itemStack2, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if(invSlot == 41) {
                if(!this.insertItem(itemStack2, 36, 37, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if(invSlot == 42) {
                if(!this.insertItem(itemStack2, 37, 38, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if(invSlot == 43) {
                if(!this.insertItem(itemStack2, 38, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if(invSlot == 44) {
                if(!this.insertItem(itemStack2, 39, 40, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if(invSlot == 81) {
                if(!this.insertItem(itemStack2, 40, 41, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if(invSlot >= 45 && invSlot < 81) {
                if(!this.insertItem(itemStack2, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
        this.storedInventory.onClose(player);
        if (player instanceof ServerPlayerEntity) {
            if(isRuined() && storedInventory != null) {
                this.dropInventory(player, storedInventory);
                ((OverworldInventoryStorage)player).setHasStoredInventory(false);
            }
        }
    }

    public void addInventorySlots() {
        if(storedInventory == null) {
            return;
        }
        storedInventory.onOpen(playerInventory.player);
        inventory.onOpen(playerInventory.player);
        int m;
        int l;
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + (m + 1) * 9, 8 + l * 18, 164 + m * 18));
            }
        }
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 222));
        }
        for (m = 0; m < 4; ++m) {
            final EquipmentSlot j = EQUIPMENT_SLOT_ORDER[m];
            this.addSlot(new Slot(playerInventory, 39 - m, 91, 8 + m * 18){
                @Override
                public int getMaxItemCount() {
                    return 1;
                }

                @Override
                public boolean canInsert(ItemStack stack) {
                    return j == MobEntity.getPreferredEquipmentSlot(stack);
                }

                @Override
                public boolean canTakeItems(PlayerEntity playerEntity) {
                    ItemStack itemStack = this.getStack();
                    if (!itemStack.isEmpty() && !playerEntity.isCreative() && EnchantmentHelper.hasBindingCurse(itemStack)) {
                        return false;
                    }
                    return super.canTakeItems(playerEntity);
                }

                @Override
                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[j.getEntitySlotId()]);
                }
            });
        }
        this.addSlot(new Slot(playerInventory, 40, 114, 62){
            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_OFFHAND_ARMOR_SLOT);
            }
        });

        for (m = 0; m < 4; ++m) {
            final EquipmentSlot j = EQUIPMENT_SLOT_ORDER[m];
            this.addSlot(new NonInsertSlot(storedInventory, 39 - m, 45, 8 + m * 18){
                @Override
                public int getMaxItemCount() {
                    return 1;
                }

                @Override
                public boolean canTakeItems(PlayerEntity playerEntity) {
                    ItemStack itemStack = this.getStack();
                    if (!itemStack.isEmpty() && !playerEntity.isCreative() && EnchantmentHelper.hasBindingCurse(itemStack)) {
                        return false;
                    }
                    return super.canTakeItems(playerEntity);
                }

                @Override
                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[j.getEntitySlotId()]);
                }
            });
        }
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new NonInsertSlot(storedInventory, l + (m + 1) * 9, 8 + l * 18, 84 + m * 18));
            }
        }
        for (m = 0; m < 9; ++m) {
            this.addSlot(new NonInsertSlot(storedInventory, m, 8 + m * 18, 142));
        }
        this.addSlot(new NonInsertSlot(storedInventory, 40, 68, 62){
            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_OFFHAND_ARMOR_SLOT);
            }
        });
        this.addSlot(new Slot(inventory, 0, 1000, 1000));
    }

    public void addPortalSlots() {
        if(storedInventory == null) {
            return;
        }
        inventory.onOpen(playerInventory.player);
        storedInventory.onOpen(playerInventory.player);
        int m;
        int l;
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
        for (m = 0; m < 4; ++m) {
            final EquipmentSlot j = EQUIPMENT_SLOT_ORDER[m];
            this.addSlot(new Slot(playerInventory, 39 - m, 1000, 1000){
                @Override
                public int getMaxItemCount() {
                    return 1;
                }

                @Override
                public boolean canInsert(ItemStack stack) {
                    return j == MobEntity.getPreferredEquipmentSlot(stack);
                }

                @Override
                public boolean canTakeItems(PlayerEntity playerEntity) {
                    ItemStack itemStack = this.getStack();
                    if (!itemStack.isEmpty() && !playerEntity.isCreative() && EnchantmentHelper.hasBindingCurse(itemStack)) {
                        return false;
                    }
                    return super.canTakeItems(playerEntity);
                }

                @Override
                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[j.getEntitySlotId()]);
                }
            });
        }
        this.addSlot(new Slot(playerInventory, 40, 1000, 1000)
        {
            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_OFFHAND_ARMOR_SLOT);
            }
        });

        for (m = 0; m < 4; ++m) {
            final EquipmentSlot j = EQUIPMENT_SLOT_ORDER[m];
            this.addSlot(new NonInsertSlot(storedInventory, 39 - m, 1000, 1000){
                @Override
                public int getMaxItemCount() {
                    return 1;
                }

                @Override
                public boolean canTakeItems(PlayerEntity playerEntity) {
                    ItemStack itemStack = this.getStack();
                    if (!itemStack.isEmpty() && !playerEntity.isCreative() && EnchantmentHelper.hasBindingCurse(itemStack)) {
                        return false;
                    }
                    return super.canTakeItems(playerEntity);
                }

                @Override
                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[j.getEntitySlotId()]);
                }
            });
        }
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new NonInsertSlot(storedInventory, l + (m + 1) * 9, 1000, 1000));
            }
        }
        for (m = 0; m < 9; ++m) {
            this.addSlot(new NonInsertSlot(storedInventory, m, 1000, 1000));
        }
        this.addSlot(new NonInsertSlot(storedInventory, 40, 1000, 1000){
            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_OFFHAND_ARMOR_SLOT);
            }
        });
        this.addSlot(new Slot(inventory, 0, 80, 55));
    }

    public boolean isRuined() {
        return true;
    }

    public int getCharge() {
        return propertyDelegate.get(0);
    }
}
