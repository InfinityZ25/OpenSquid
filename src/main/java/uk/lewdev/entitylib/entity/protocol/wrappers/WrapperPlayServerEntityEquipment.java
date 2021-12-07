package uk.lewdev.entitylib.entity.protocol.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import uk.lewdev.entitylib.utils.MCVersion;

import java.util.ArrayList;
import java.util.List;

public class WrapperPlayServerEntityEquipment extends AbstractPacket {

    public static final PacketType TYPE =
      PacketType.Play.Server.ENTITY_EQUIPMENT;

    private ItemSlot slot;
    private ItemStack item;

    public WrapperPlayServerEntityEquipment() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerEntityEquipment(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve Entity ID.
     * <p>
     * Notes: entity's ID
     *
     * @return The current Entity ID
     */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set Entity ID.
     *
     * @param value - new value.
     */
    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve the entity of the painting that will be spawned.
     *
     * @param world - the current world of the entity.
     * @return The spawned entity.
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity of the painting that will be spawned.
     *
     * @param event - the packet event.
     * @return The spawned entity.
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    public ItemSlot getSlot() {
        return this.slot;
    }

    public void setSlot(ItemSlot value) {
        this.slot = value;

        if (MCVersion.getCurrentVersion().isAfterOr1_16()) {
            this.update1_16Packet();
        } else {
            handle.getItemSlots().write(0, value);
        }
    }

    /**
     * Retrieve Item.
     * <p>
     * Notes: item in slot format
     *
     * @return The current Item
     */
    public ItemStack getItem() {
        return this.item;
    }

    /**
     * Set Item.
     *
     * @param value - new value.
     */
    public void setItem(ItemStack value) {
        this.item = value;

        if (MCVersion.getCurrentVersion().isAfterOr1_16()) {
            this.update1_16Packet();
        } else {
            handle.getItemModifier().write(0, value);
        }
    }

    private void update1_16Packet() {
        if (this.slot == null || this.item == null) {
            return;
        }

        List<Pair<ItemSlot, ItemStack>> items = new ArrayList<>();

        Pair<ItemSlot, ItemStack> pair = new Pair<>(this.slot, this.item);
        items.add(pair);

        handle.getSlotStackPairLists().write(0, items);
    }
}