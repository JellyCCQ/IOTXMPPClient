package jelly.xmppclient.manager;



import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;

import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import java.util.ArrayList;
import java.util.Collection;

import jelly.xmppclient.provider.GetDataRespProvider;
import jelly.xmppclient.provider.SubscribRespProvider;
import jelly.xmppclient.provider.UnsubNodeReqProvider;
import jelly.xmppclient.provider.WriteNodeRespProvider;
import jelly.xmppclient.util.ConstUtil;

/**
 * Created by 陈超钦 on 2018/5/26.
 */

public class XmppManager {

    private static XmppManager xmppconnectionManager;
    private XMPPConnection xmppConnection;

    private XmppManager() {
    }


    public synchronized static XmppManager getXmppconnectionManager() {
        if (xmppconnectionManager == null) {
            xmppconnectionManager = new XmppManager();
        }
        return xmppconnectionManager;
    }

    public XMPPConnection initConnection() {
        ConnectionConfiguration configuration = new ConnectionConfiguration(ConstUtil.SERVER_HOST, ConstUtil.SERVER_PORT);
        // 开启debug
        configuration.setDebuggerEnabled(true);
//        // 关闭安全验证登录方式
//        configuration.setSASLAuthenticationEnabled(false);
//        // 数据是否使用安全模式
//        configuration.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
//        // 设置数据是否压缩
//        configuration.setCompressionEnabled(false);
        // 是否给服务器发送在线状态
        configuration.setSendPresence(true);
//        // 设置是否重新自动连接服务器
        configuration.setReconnectionAllowed(true);
//        // asmack bug
        configure(ProviderManager.getInstance());
        configuration.setRosterLoadedAtLogin(false);
        xmppConnection = new XMPPConnection(configuration);
        return xmppConnection;
    }


    /**
     * 获取XmppConnection
     *
     * @return
     */
    public XMPPConnection getXmppConnection() {
        if (xmppConnection != null) {
            Roster ros=xmppConnection.getRoster();
            Collection<RosterGroup> entriesGroup=ros.getGroups();
            for (RosterGroup group:entriesGroup){
                Collection<RosterEntry> entries=group.getEntries();
                System.out.println("xmppConnectionGroup:"+group.getName());
                for (RosterEntry entry:entries){
                    System.out.println("xmppConnectionName:"+entry.getName());
                }
            }
            if (!xmppConnection.isConnected()) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            xmppConnection.connect();
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
            return xmppConnection;
        } else {
            return initConnection();
        }
    }

    public void addfriend(){
        try {
            xmppConnection.getRoster().createGroup( "A" );
            xmppConnection.getRoster().createEntry( "A1","A1",null );
        } catch (XMPPException e) {
            System.out.println("xmppConnectionaddfriend error");
        }
    }
    /**
     * 获取花名册
     *
     * @return
     */
    public Roster getRoster() {
        Roster roster = getXmppConnection().getRoster();
        return roster;
    }


    /**
     * 获取当前登录用户的所有分组
     */

    public ArrayList<RosterGroup> getGroups() {
        Roster roster = getXmppConnection().getRoster();
        return new ArrayList<>(roster.getGroups());
    }

    /**
     * 发送消息
     *
     * @param content
     * @param toUser
     * @return
     */
    public static boolean sendMessage(String content, String toUser) {
        ChatManager chatManager = xmppconnectionManager.getXmppConnection().getChatManager();
        Chat chat = chatManager.createChat(toUser, null);
        if (chat != null) {
            try {
                chat.sendMessage(content);
                return true;
            } catch (XMPPException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }


    /**
     * 为了解决asmack潜在bug需要实现的方法
     *
     * @param pm
     */
    public void configure(ProviderManager pm) {

        /**
         * 读取数据解析器
         */
        pm.addIQProvider("resp", "get:data", new GetDataRespProvider());
        /**
         * 订阅响应解析器
         */
        pm.addIQProvider("resp", "resp:subd", new SubscribRespProvider());

        /**
         * 写入数据的响应解析器
         */
        pm.addIQProvider("resp", "write:data", new WriteNodeRespProvider());

        /**
         * 取消订阅的响应解析器
         */
        pm.addIQProvider("req", "req:usubd", new UnsubNodeReqProvider());


        pm.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());
        // Time
        try {
            pm.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Roster Exchange
        pm.addExtensionProvider("x", "jabber:x:roster", new RosterExchangeProvider());
        // Message Events
        pm.addExtensionProvider("x", "jabber:x:event", new MessageEventProvider());
        // Chat State
        pm.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        // XHTML
        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());
        // Group Chat Invitations
        pm.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider());
        // Service Discovery # Items //解析房间列表
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
        // Service Discovery # Info //某一个房间的信息
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
        // Data Forms
        pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
        // MUC User
        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());
        // MUC Admin
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());
        // MUC Owner
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());
        // Delayed Delivery
        pm.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());
        // Version
        try {
            pm.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            // Not sure what's happening here.
        }
        // VCard
        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
        // Offline Message Requests
        pm.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());
        // Offline Message Indicator
        pm.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());
        // Last Activity
        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
        // User Search
        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
        // SharedGroupsInfo
        pm.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());
        // JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());
        pm.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());
        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
        pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
        pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
        pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
        pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
        pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
        pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
        pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());

    }

}
