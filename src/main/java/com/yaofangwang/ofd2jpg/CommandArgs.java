package com.yaofangwang.ofd2jpg;

class CommandArgs {
    int dpi = 300;
    String ofdFilePath;
    String outDirPath;
    boolean showVersion = false;
    boolean showHelp = false;
    boolean showError = false;

    CommandArgs(String[] args){
        if(args == null || args.length == 0){
            handleError("missing arguments");
            return;
        }
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            // 处理长选项
            if (arg.startsWith("--")) {
                String[] parts = arg.substring(2).split("=", 2);
                String option = parts[0];
                String value = parts.length > 1 ? parts[1] : null;

                switch (option) {
                    case "help": {
                        showHelp = true;
                        return;
                    }
                    case "version": {
                        showVersion = true;
                        return;
                    }
                    case "dip": {
                        if (value == null) handleError("--dip needs a DPI value");
                        dpi = parseInt(value);
                    }
                    case "output": {
                        if (value == null) handleError("--output needs a directory path");
                        outDirPath = value;
                    }
                    default: handleError("unknown option: --" + option);
                }
            }
            // 处理短选项
            else if (arg.startsWith("-")) {
                String optPart = arg.substring(1);
                String[] parts = optPart.split("=", 2);
                char option = parts[0].charAt(0);
                String value = parts.length > 1 ? parts[1] :
                        (parts[0].length() > 1 ? parts[0].substring(1) : null);

                switch (option) {
                    case 'h':
                    case '?':
                        showHelp = true;
                        return;
                    case 'v':
                        showVersion = true;
                        return;
                    case 'd':
                        if (value == null && ++i < args.length) value = args[i];
                        if (value == null) handleError("-d needs a DPI value");
                        dpi = parseInt(value);
                        break;
                    case 'o':
                        if (value == null && ++i < args.length) value = args[i];
                        if (value == null) handleError("-o needs a directory path");
                        outDirPath = value;
                        break;
                    default:
                        handleError("unknown option: -" + option);
                }
            }
            // 处理输入文件
            else {
                if (ofdFilePath != null) handleError("only one OFD file can be converted at a time");
                ofdFilePath = arg;
            }
        }
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            handleError("invalid int argument: " + value);
            return -1; // 实际不会执行
        }
    }

    private void handleError(String message) {
        showError = true;
        System.err.println("error: " + message);
        System.err.println("use --help to see usage information");
        System.exit(1);
    }
}
