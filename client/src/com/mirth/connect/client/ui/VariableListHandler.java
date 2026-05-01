/*
 * Copyright (c) Mirth Corporation. All rights reserved.
 * 
 * http://www.mirthcorp.com
 * 
 * The software in this package is published under the terms of the MPL license a copy of which has
 * been included with this distribution in the LICENSE.txt file.
 */

package com.mirth.connect.client.ui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.jdesktop.swingx.JXList;

import com.mirth.connect.client.ui.i18n.I18n;
import com.mirth.connect.client.ui.panels.reference.ReferenceTable;
import com.mirth.connect.model.Connector;
import com.mirth.connect.model.util.DefaultMetaData;

public class VariableListHandler extends TransferHandler {

    public enum TransferMode {
        RAW("", ""), VELOCITY("${", "}"), JAVASCRIPT("$('", "')");

        private String prefix;
        private String suffix;

        private TransferMode(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getSuffix() {
            return suffix;
        }
    };

    private TransferMode transferMode;
    private Map<String, Integer> metaDataMap = new HashMap<String, Integer>();
    private static Map<String, String> staticJsReferences;
    private static Map<String, String> staticVelocityReferences;

    static {
        staticVelocityReferences = new HashMap<String, String>();
        staticVelocityReferences.put(I18n.t("reference.static.rawData", "Raw Data"), "${message.rawData}");
        staticVelocityReferences.put(I18n.t("reference.static.transformedData", "Transformed Data"), "${message.transformedData}");
        staticVelocityReferences.put(I18n.t("reference.static.messageId", "Message ID"), "${message.messageId}");
        staticVelocityReferences.put(I18n.t("reference.static.encodedData", "Encoded Data"), "${message.encodedData}");
        staticVelocityReferences.put(I18n.t("reference.static.messageSource", "Message Source"), "${" + DefaultMetaData.SOURCE_VARIABLE_MAPPING + "}");
        staticVelocityReferences.put(I18n.t("reference.static.messageType", "Message Type"), "${" + DefaultMetaData.TYPE_VARIABLE_MAPPING + "}");
        staticVelocityReferences.put(I18n.t("reference.static.messageVersion", "Message Version"), "${" + DefaultMetaData.VERSION_VARIABLE_MAPPING + "}");
        staticVelocityReferences.put(I18n.t("reference.static.messageHash", "Message Hash"), "${HASH}");
        staticVelocityReferences.put(I18n.t("reference.static.timestamp", "Timestamp"), "${SYSTIME}");
        staticVelocityReferences.put(I18n.t("reference.static.uniqueId", "Unique ID"), "${UUID}");
        staticVelocityReferences.put(I18n.t("reference.static.date", "Date"), "${DATE}");
        staticVelocityReferences.put(I18n.t("reference.static.originalFileName", "Original File Name"), "${originalFilename}");
        staticVelocityReferences.put(I18n.t("reference.static.count", "Count"), "${COUNT}");
        staticVelocityReferences.put(I18n.t("reference.static.dicomMessageRawData", "DICOM Message Raw Data"), "${DICOMMESSAGE}");
        staticVelocityReferences.put(I18n.t("reference.static.formattedDate", "Formatted Date"), "${date.get('yyyy-M-d H.m.s')}");
        staticVelocityReferences.put(I18n.t("reference.static.xmlEntityEncoder", "XML Entity Encoder"), "${XmlUtil.encode()}");
        staticVelocityReferences.put(I18n.t("reference.static.xmlPrettyPrinter", "XML Pretty Printer"), "${XmlUtil.prettyPrint()}");
        staticVelocityReferences.put(I18n.t("reference.static.escapeJsonString", "Escape JSON String"), "${JsonUtil.escape()}");
        staticVelocityReferences.put(I18n.t("reference.static.jsonPrettyPrinter", "JSON Pretty Printer"), "${JsonUtil.prettyPrint()}");
      
        // these are used in DataPrunerPanel
        staticVelocityReferences.put(I18n.t("reference.static.serverId", "Server ID"), "${message.serverId}");
        staticVelocityReferences.put(I18n.t("reference.static.channelId", "Channel ID"), "${message.channelId}");
        staticVelocityReferences.put(I18n.t("reference.static.channelName", "Channel Name"), "${message.channelName}");
        staticVelocityReferences.put(I18n.t("reference.static.formattedMessageDate", "Formatted Message Date"), "${date.format('yyyy-MM-dd',$message.getConnectorMessages().get(0).getReceivedDate())}");
        staticVelocityReferences.put(I18n.t("reference.static.formattedCurrentDate", "Formatted Current Date"), "${date.get('yyyy-MM-dd')}");

        staticJsReferences = new HashMap<String, String>();
        staticJsReferences.put(I18n.t("reference.static.rawData", "Raw Data"), "connectorMessage.getRawData()");
        staticJsReferences.put(I18n.t("reference.static.transformedData", "Transformed Data"), "connectorMessage.getTransformedData()");
        staticJsReferences.put(I18n.t("reference.static.messageId", "Message ID"), "connectorMessage.getMessageId()");
        staticJsReferences.put(I18n.t("reference.static.encodedData", "Encoded Data"), "connectorMessage.getEncodedData()");
        staticJsReferences.put(I18n.t("reference.static.messageSource", "Message Source"), "$('" + DefaultMetaData.SOURCE_VARIABLE_MAPPING + "')");
        staticJsReferences.put(I18n.t("reference.static.messageType", "Message Type"), "$('" + DefaultMetaData.TYPE_VARIABLE_MAPPING + "')");
        staticJsReferences.put(I18n.t("reference.static.messageHash", "Message Hash"), "HashUtil.generate(connectorMessage.getEncodedData())");

        staticJsReferences.put(I18n.t("reference.static.messageVersion", "Message Version"), "$('" + DefaultMetaData.VERSION_VARIABLE_MAPPING + "')");
        staticJsReferences.put(I18n.t("reference.static.timestamp", "Timestamp"), "var dateString = DateUtil.getCurrentDate('yyyyMMddHHmmss');");
        staticJsReferences.put(I18n.t("reference.static.uniqueId", "Unique ID"), "var uuid = UUIDGenerator.getUUID();");
        staticJsReferences.put(I18n.t("reference.static.date", "Date"), "var date = DateUtil.getDate('pattern','date');");
        staticJsReferences.put(I18n.t("reference.static.originalFileName", "Original File Name"), "$('originalFilename')");
        staticJsReferences.put(I18n.t("reference.static.dicomMessageRawData", "DICOM Message Raw Data"), "var rawData = DICOMUtil.getDICOMRawData(connectorMessage);");
        staticJsReferences.put(I18n.t("reference.static.messageWithAttachments", "Message with Attachments"), "var rawData = AttachmentUtil.reAttachMessage(connectorMessage)");
        staticJsReferences.put(I18n.t("reference.static.formattedDate", "Formatted Date"), "var dateString = DateUtil.getCurrentDate('yyyy-M-d H.m.s');");
        staticJsReferences.put(I18n.t("reference.static.xmlEntityEncoder", "XML Entity Encoder"), "var encodedMessage = XmlUtil.encode('message');");
        staticJsReferences.put(I18n.t("reference.static.xmlPrettyPrinter", "XML Pretty Printer"), "var prettyPrintedMessage = XmlUtil.prettyPrint('message');");
        staticJsReferences.put(I18n.t("reference.static.escapeJsonString", "Escape JSON String"), "var escapedJSONString = JsonUtil.escape('message');");
        staticJsReferences.put(I18n.t("reference.static.jsonPrettyPrinter", "JSON Pretty Printer"), "var prettyPrintedMessage = JsonUtil.prettyPrint('message');");
    }

    public VariableListHandler(TransferMode transferMode) {
        this(transferMode, null);
    }

    public VariableListHandler(TransferMode transferMode, List<Connector> connectors) {
        this.transferMode = transferMode;
        populateConnectors(connectors);
    }

    public TransferMode getTransferMode() {
        return transferMode;
    }

    public void setTransferMode(TransferMode transferMode) {
        this.transferMode = transferMode;
    }

    public void populateConnectors(List<Connector> connectors) {
        if (connectors != null) {
            for (Connector connector : connectors) {
                metaDataMap.put(connector.getName(), connector.getMetaDataId());
            }
        }
    }

    protected Transferable createTransferable(JComponent c) {
        try {
            String text = "";
            if (c instanceof JXList) {
                JXList list = ((JXList) (c));
                if (list == null) {
                    return null;
                }
                text = (String) list.getSelectedValue();
            } else if (c instanceof ReferenceTable) {
                ReferenceTable reftable = ((ReferenceTable) (c));
                if (reftable == null) {
                    return null;
                }

                int currRow = reftable.getSelectedRow();

                if (currRow >= 0 && currRow < reftable.getRowCount()) {
                    text = (String) reftable.getValueAt(currRow, 0);
                }
            }

            if (text != null) {
                if (transferMode == TransferMode.VELOCITY && staticVelocityReferences.containsKey(text)) {
                    return new VariableTransferable(staticVelocityReferences.get(text), TransferMode.RAW, metaDataMap);
                } else if (transferMode == TransferMode.JAVASCRIPT && staticJsReferences.containsKey(text)) {
                    return new VariableTransferable(staticJsReferences.get(text), TransferMode.RAW, metaDataMap);
                }

                return new VariableTransferable(text, transferMode, metaDataMap);
            }
            return null;
        } catch (ClassCastException cce) {
            return null;
        }
    }

    public int getSourceActions(JComponent c) {
        return COPY;
    }

    public boolean canImport(JComponent c, DataFlavor[] df) {
        return false;
    }
}
