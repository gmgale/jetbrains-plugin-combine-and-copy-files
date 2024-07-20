package com.george.gale;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.ui.Messages;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CombineFilesAction extends AnAction implements ClipboardOwner {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Get selected files
        VirtualFile[] files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);

        if (files == null || files.length == 0) {
            Messages.showInfoMessage("No files selected", "Information");
            return;
        }

        // Find the common base path
        String commonBasePath = findCommonBasePath(files);
        if (commonBasePath == null) {
            Messages.showErrorDialog("Could not determine a common directory for the selected files", "Error");
            return;
        }

        // Combine contents
        StringBuilder combinedContent = new StringBuilder();
        for (VirtualFile file : files) {
            String relativePath = getRelativePath(commonBasePath, file.getPath());
            combinedContent.append("=== ").append(relativePath).append(" ===\n");
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
            clipboard.setContents(stringSelection, this);
        } catch (Exception ex) {
            ex.printStackTrace();
            Messages.showErrorDialog("Failed to copy to clipboard: " + ex.getMessage(), "Error");
        }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // No action needed when clipboard ownership is lost
    }

    private String findCommonBasePath(VirtualFile[] files) {
        Path commonPath = Paths.get(files[0].getPath());
        for (VirtualFile file : files) {
            commonPath = commonPath.getParent();
            Path filePath = Paths.get(file.getPath());
            while (!filePath.startsWith(commonPath)) {
                commonPath = commonPath.getParent();
            }
        }
        return commonPath.toString();
    }

    private String getRelativePath(String basePath, String fullPath) {
        Path base = Paths.get(basePath);
        Path full = Paths.get(fullPath);
        return base.relativize(full).toString();
    }
}
