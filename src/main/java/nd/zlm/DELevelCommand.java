package nd.zlm;

import com.google.common.collect.Lists;
import com.typesafe.config.ConfigException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.io.IOException;
import org.jsoup.Jsoup;//jsoup https://jsoup.org/
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DELevelCommand extends CommandBase {

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    @Nonnull
    public String getName() {
        return "zlm";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "zlm <map id> <Do write in chat?>";
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        int mapId = Integer.parseInt(args[0]);
        boolean writeInChat = Boolean.valueOf(args[1]);
        getPlayersDataAndShow(mapId, writeInChat);
    }

    public static  void getPlayersDataAndShow(int mapId, boolean writeInChat){
        Minecraft mc = Minecraft.getMinecraft();
        mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString("Zombies Level is..."));
        Collection<NetworkPlayerInfo> playersC=mc.getConnection().getPlayerInfoMap();

        for(NetworkPlayerInfo loadedPlayer: playersC){
            String loadedPlayerName = loadedPlayer.getGameProfile().getName();
            GetDataAndShow gdas = new GetDataAndShow(loadedPlayerName, mapId, writeInChat);
            gdas.start();
        }
    }

    public static  class GetDataAndShow extends  Thread{
        String playerN;
        int mapId;//mapId: 3=table head, 2=aileen arcadium, 1=bad blood, 1=dead end
        boolean writeC = false;

        public GetDataAndShow(String playerName, int mapId_, boolean writeInChat){
            playerN = playerName;
            mapId = mapId_;
            writeC = writeInChat;
        }

        public void run(){
            Minecraft mc = Minecraft.getMinecraft();
            try {

                Document doc = Jsoup.connect("https://plancke.io/hypixel/player/stats/"+playerN+"#Arcade").get();

                Elements tables = doc.select("table");
                Element table = tables.get(0);
                Elements rows = table.select("tr");
                Element rowh = rows.get(0);//header
                Element row = rows.get(3-mapId);
                Elements tds = row.select("td");
                Elements htds = rowh.select("td");
                String str = playerN+"/ ";
                //Element htd = htds.get(5);
                //String htdt = htd.text();
                Element td = tds.get(5);
                String tdt = td.text();
                str += "Kill:"+tdt+" ";
                //htd = htds.get(7);
                //htdt = htd.text();
                td = tds.get(7);
                tdt = td.text();
                str += "Best:r"+tdt+" ";
                mc.ingameGUI.addChatMessage(ChatType.SYSTEM, new TextComponentString(str));
                if(writeC)mc.player.sendChatMessage(str);
            }catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
