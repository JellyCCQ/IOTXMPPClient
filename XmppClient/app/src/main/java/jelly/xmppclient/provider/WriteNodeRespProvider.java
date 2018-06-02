package jelly.xmppclient.provider;

import jelly.xmppclient.packet.WriteNodeResp;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;


public class WriteNodeRespProvider implements IQProvider {
    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        return new WriteNodeResp();
    }
}
