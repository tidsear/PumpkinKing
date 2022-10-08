package art.tidsear.pumpkininterface;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.util.vector.Vector3f;
import scala.collection.parallel.ParIterableLike;

import java.util.ArrayList;
import java.util.List;

public class InternalCommandsImpl implements InternalCommands{
    public InternalCommandsImpl() {

    }

    @Override
    public List<String> getServerPlayers() {
        List<String> players = new ArrayList<>();
        List<EntityPlayerMP> playerEntities = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        for (EntityPlayerMP p : playerEntities) {
            players.add(p.getDisplayName());
        }
        return players;
    }

    @Override
    public void teleportPlayer(String playerName, Vector3f newPos) {

    }

    @Override
    public void sendMessageAll(String message) {
        MinecraftServer.getServer().addChatMessage(new ChatComponentText(message));
    }
}