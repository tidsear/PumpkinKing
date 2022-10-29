package art.tidsear.pumpkinpoints;

import art.tidsear.pumpkinking.PumpkinKingMod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.Sys;

import java.awt.*;
import java.util.Objects;

public class PointsOverlay {
    Color translucentBlack = new Color(0,0,0,0.7f);
    int trbl = translucentBlack.getRGB();
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        int scaledHeight = event.resolution.getScaledHeight();
        int scaledWidth = event.resolution.getScaledWidth();
        String role = PumpkinKingMod.myData.playerRole;


        // Would render this all the time, but the UI is already cluttered by Decimation
        if(Minecraft.getMinecraft().gameSettings.keyBindPlayerList.getIsKeyPressed()) {
            // Draw backdrop (Specifically designed to go over decimation character overlay, unfortunately translucency might not work out bc of that)
            Gui.drawRect(10,0+10,130,120,trbl);

            // Draw Player points in black box
            Minecraft.getMinecraft().ingameGUI.drawString(Minecraft.getMinecraft().fontRenderer,"Points: " + String.valueOf(PumpkinKingMod.myData.playerPoints), 15, 0+15, Color.ORANGE.getRGB());

            // TODO Draw Current Objective
            String objective = "Objective: None";

            if (Objects.equals(role, "King")) {
                objective = "Objective: Protect the pumpkin core, kill humans, hinder human objectives.";
            } else if (Objects.equals(role, "Crew")) {

            }

            // Will probably want split string for Objectives
            Minecraft.getMinecraft().fontRenderer.drawSplitString(objective, 15, 0+25, 105, Color.WHITE.getRGB());

            // TODO Draw Remaining Number Of Objectives, Probably want to put this above current objective
        }


        // Draw Player Role All the time
        int roleColor = Color.WHITE.getRGB();
        String roleText = "No Role";

        // Probably want constants or enum for this role idk
        if(Objects.equals(role, "King")) {
            roleColor = Color.RED.getRGB();
            roleText = "You Are The Pumpkin King";
        } else if (Objects.equals(role, "Crew")) {
            roleColor = Color.GREEN.getRGB();
            roleText = "You Are Crew";
        }
        Minecraft.getMinecraft().ingameGUI.drawCenteredString(Minecraft.getMinecraft().fontRenderer, roleText, scaledWidth/2, scaledHeight-50, roleColor);
    }
}