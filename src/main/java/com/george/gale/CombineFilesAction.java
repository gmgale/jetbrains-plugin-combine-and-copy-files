package com.george.gale;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.ui.Messages;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;

public class CombineFilesAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Get selected files
        VirtualFile[] files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);

        if (files == null || files.length == 0) {
            Messages.showInfoMessage("No files selected", "Information");
            return;
        }

        // Combine contents
        StringBuilder combinedContent = new StringBuilder();
        for (VirtualFile file : files) {
            combinedContent.append("=== ").append(file.getName()).append(" ===\n");
            try {
                combinedContent.append(new String(file.contentsToByteArray())).append("\n");
            } catch (IOException ex) {
                ex.printStackTrace();
                Messages.showErrorDialog("Failed to read file: " + file.getName(), "Error");
                return;
            }
        }

        // Copy combined content to clipboard
        try {
            StringSelection stringSelection = new StringSelection(combinedContent.toString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            Messages.showInfoMessage("Files combined and copied to clipboard successfully", "Success");
        } catch (Exception ex) {
            ex.printStackTrace();
            Messages.showErrorDialog("Failed to copy to clipboard: " + ex.getMessage(), "Error");
        }
    }
}
