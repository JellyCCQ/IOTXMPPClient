package jelly.xmppclient.packet;

import org.jivesoftware.smack.packet.IQ;

/**
 * Created by tiandawu on 2016/4/14.
 */
public class GetDataResp extends IQ {


    private String var;
    private String value;

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getChildElementXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<resp xmlns='get:data'><attr var='");
        buf.append(getVar());
        buf.append("'>");
        buf.append(getValue());
        buf.append("</attr></resp>");
        return buf.toString();
    }
}
