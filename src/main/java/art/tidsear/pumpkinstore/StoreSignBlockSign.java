package art.tidsear.pumpkinstore;

import art.tidsear.pumpkingamemode.PKState;
import art.tidsear.pumpkinking.PumpkinKingMod;
import art.tidsear.utility.ForgeGive;
import art.tidsear.utility.Vector3f;
import net.minecraft.block.BlockSign;
import net.minecraft.command.CommandGive;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import org.jetbrains.annotations.NotNull;
import scala.Int;

public class StoreSignBlockSign extends BlockSign {

    private ICommandSender sender;
    private ForgeGive cmdGive;
    public StoreSignBlockSign(boolean onGround) {
        super(TileEntitySign.class, onGround);
        cmdGive = new ForgeGive();
        sender = new StoreSender();
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        try {
            TileEntitySign te = TileEntitySign.class.newInstance();
            return te;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public boolean onBlockActivated(@NotNull World world, int x, int y, int z, EntityPlayer player, int handIn, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "random.click", 0.3F, 0.6F);

        TileEntitySign tes = (TileEntitySign) world.getTileEntity(x,y,z);

        if (tes == null) {
            System.out.println("tes was null");
            return false;
        }

        boolean wasRandom = false;
        int cost = parseBuySign(tes.signText[0]);
        if (cost == -1){
            cost = parseRandomSign(tes.signText[0]);
            if (cost != -1) {
                wasRandom = true;
            }
        }
        if (cost == -1 || cost < 0) return false;

        if (player.capabilities.isCreativeMode) {
            player.addChatMessage(new ChatComponentText("Cost was: "+cost));
            if (!wasRandom) {
                cmdGive.processCommand(this.sender, new String[]{player.getDisplayName(), tes.signText[1] + ":" + tes.signText[2] + tes.signText[3]});
            } else {
                Vector3f boxPos = parseV3f1Line(tes.signText[3]);
                TileEntity chestTes = world.getTileEntity((int) boxPos.getX(), (int) boxPos.getY(), (int) boxPos.getZ());
                if (!(chestTes instanceof TileEntityChest)) {
                    return false;
                }
                ItemStack itemStack = ((TileEntityChest)chestTes).getStackInSlot(chestRandomSlot(world));
                boolean possible = player.inventory.addItemStackToInventory(itemStack.copy());
                if (!possible) {
                    player.dropPlayerItemWithRandomChoice(itemStack, false);
                }
                player.inventoryContainer.detectAndSendChanges();
            }
        } else if (PumpkinKingMod.pkGameMode.GetState() != PKState.IDLE) {
            if (PumpkinKingMod.pkGameMode.GetPtsSystem().WithdrawPoints(player.getDisplayName(),cost)) {
                if (!wasRandom) {
                    cmdGive.processCommand(this.sender, new String[]{player.getDisplayName(), tes.signText[1] + ":" + tes.signText[2] + tes.signText[3]});
                } else {
                    Vector3f boxPos = parseV3f1Line(tes.signText[3]);
                    TileEntity chestTes = world.getTileEntity((int) boxPos.getX(), (int) boxPos.getY(), (int) boxPos.getZ());
                    if (!(chestTes instanceof TileEntityChest)) {
                        return false;
                    }
                    ItemStack itemStack = ((TileEntityChest)chestTes).getStackInSlot(chestRandomSlot(world));
                    // This is what I should have done everywhere, obvs now
                    // I thought addItemStackToInventory didn't work because the items were invisible
                    boolean possible = player.inventory.addItemStackToInventory(itemStack.copy());
                    if (!possible) {
                        player.dropPlayerItemWithRandomChoice(itemStack, false);
                    }
                    player.inventoryContainer.detectAndSendChanges();
                }
            } else {
                player.addChatMessage(new ChatComponentText("You don't have enough points!"));
            }
            // logic for checking / subtracting points from a player
        }
        return true;
    }

    private int parseBuySign(String line) {
        String[] parts = line.split(" ");
        if (parts.length != 2) {
            return -1;
        }
        if (!parts[0].equals("Buy")) {
            return -1;
        }

        if (parts[1].contains("pts")) {
            parts[1] = parts[1].replace("pts","");
        }
        if (parts[1].contains("PTS")) {
            parts[1] = parts[1].replace("PTS","");
        }
        if (parts[1].contains("pt")) {
            parts[1] = parts[1].replace("pt","");
        }
        if (parts[1].contains("PT")) {
            parts[1] = parts[1].replace("PT","");
        }
        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            System.out.println("exception?");
            return -1;
        }
    }

    private int parseRandomSign(String line) {
        String[] parts = line.split(" ");
        if (parts.length != 2) {
            return -1;
        }
        if (!parts[0].equals("Random")) {
            return -1;
        }

        if (parts[1].contains("pts")) {
            parts[1] = parts[1].replace("pts","");
        }
        if (parts[1].contains("PTS")) {
            parts[1] = parts[1].replace("PTS","");
        }
        if (parts[1].contains("pt")) {
            parts[1] = parts[1].replace("pt","");
        }
        if (parts[1].contains("PT")) {
            parts[1] = parts[1].replace("PT","");
        }

        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            System.out.println("exception?");
            return -1;
        }
    }

    private Vector3f parseV3f1Line(String line) {
        String[] parts = line.split(" ");
        if (parts.length != 3) {
            return new Vector3f(0,0,0);
        }
        try {
            return new Vector3f(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        } catch (Exception e) {
            return new Vector3f(0,0,0);
        }
    }

    private int chestRandomSlot(World world) {
        int max = 27;
        return world.rand.nextInt(max);
    }
}

class StoreSender implements ICommandSender {

    @Override
    public String getCommandSenderName() {
        return "Store";
    }

    @Override
    public IChatComponent func_145748_c_() {
        return new ChatComponentText(this.getCommandSenderName());
    }

    @Override
    public void addChatMessage(IChatComponent p_145747_1_) {
        System.out.println(p_145747_1_.getUnformattedText());
    }

    @Override
    public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_) {
        return true;
    }

    @Override
    public ChunkCoordinates getPlayerCoordinates() {
        return null;
    }

    @Override
    public World getEntityWorld() {
        return null;
    }
}