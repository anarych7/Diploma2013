package modules.service;

import client.gui.ClientFrame;
import modules.object.Pack;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class Shower implements Runnable {
    private final Logger log = Logger.getLogger(Shower.class.getName());
    private final LinkedBlockingQueue<Pack> dataQueue;
    private final ClientFrame clientFrame;

    public Shower(LinkedBlockingQueue<Pack> dataQueue, ClientFrame clientFrame) {
        this.dataQueue = dataQueue;
        this.clientFrame = clientFrame;
    }

    //Работа сервиса по отображению данных
    @Override
    public void run() {
        try {
            log.info("Thread is run...");
            while (!Thread.currentThread().isInterrupted()) {
                Pack pack = dataQueue.take();

                //Запуск необходимых методов отображения данных в зависимости от типа ответа в пакете
                switch (pack.getNetObject().answerType) {
                    case GENERAL:
                        toConsole(pack.getNetObject().systemMessage.toString(), pack.getNetObject().errorMessage.toString());
                        break;
                    case QM_LIST:
                        toConsole(pack.getNetObject().systemMessage.toString(), pack.getNetObject().errorMessage.toString());
                        fillTableVMs(pack.getNetObject().systemMessage.toString());
                        break;
                    case VZDUMP:
                        toConsole(pack.getNetObject().systemMessage.toString(), pack.getNetObject().errorMessage.toString());
                        break;
                    case VZRESTORE:
                        toConsole(pack.getNetObject().systemMessage.toString(), pack.getNetObject().errorMessage.toString());
                        break;
                    case DUMP_LIST:
                        toConsole(pack.getNetObject().systemMessage.toString(), pack.getNetObject().errorMessage.toString());
                        fillTableBackups(pack.getNetObject().systemMessage.toString());
                        break;
                    case LIST_FROM_XML:
                        toConsole(pack.getNetObject().systemMessage.toString(), pack.getNetObject().errorMessage.toString());
                        fillTableServers(pack.getNetObject().systemMessage.toString());
                        break;
                    case TASK_LIST:
                        toConsole(pack.getNetObject().systemMessage.toString(), pack.getNetObject().errorMessage.toString());
                        fillTableTasks(pack.getNetObject().systemMessage.toString());
                        break;
                    default:
                        System.out.println(pack.getNetObject().systemMessage.toString());
                        System.out.println(pack.getNetObject().errorMessage.toString());
                }
            }
        } catch (InterruptedException e) {
            dataQueue.clear();
            Thread.currentThread().interrupt();
            log.info("Thread is stopped.");
        }
    }

    //Отображение данных в "консоли" клиентского приложения
    private void toConsole(final String systemMessage, final String errorMassage) {
        final String toConsole;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SYSTEM_MESSAGE:\n");
        stringBuilder.append(systemMessage);
        stringBuilder.append("ERROR_MESSAGE:\n");
        stringBuilder.append(errorMassage);
        stringBuilder.append("\n");
        toConsole = stringBuilder.toString();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                clientFrame.getTextAreaConsole().setText(toConsole);
            }
        });
    }

    //Заполнение таблицы серверов
    private void fillTableServers(String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, " \t\n\r:|");
        int count = stringTokenizer.countTokens();
        int indexI = count / 2;
        int indexJ = 2;
        Object[][] data = new Object[indexI][indexJ];
        for (int i = 0; i < indexI; i++) {
            for (int j = 0; j < indexJ; j++) {
                data[i][j] = stringTokenizer.nextElement();
            }
        }
        final DefaultTableModel model = new TableModel();
        String[] headings = {"IP", "PORT"};
        model.setDataVector(data, headings);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JTable tempTable = clientFrame.getTableServers();
                tempTable.setModel(model);
            }
        });
    }

    //Заполнение таблицы виртуальных серверов
    private void fillTableVMs(String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, " \t\n\r");
        int count = stringTokenizer.countTokens() - 6;
        int indexI = count / 6;
        int indexJ = 6;
        String[] headings = new String[6];
        for (int j = 0; j < indexJ; j++) {
            headings[j] = stringTokenizer.nextToken();
        }
        Object[][] data = new Object[indexI][indexJ];
        for (int i = 0; i < indexI; i++) {
            for (int j = 0; j < indexJ; j++) {
                data[i][j] = stringTokenizer.nextElement();
            }
        }
        final DefaultTableModel model = new TableModel();
        model.setDataVector(data, headings);
        final DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel();
        for (int i = 0; i < indexI; i++) {
            defaultComboBoxModel.addElement(data[i][0]);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JTable tempTable = clientFrame.getTableVMs();
                tempTable.setModel(model);
                clientFrame.getComboBoxVMID().setModel(defaultComboBoxModel);
            }
        });
    }

    //Заполнение таблицы резервных копий
    private void fillTableBackups(String string) {
        System.out.println(string);
        StringTokenizer stringTokenizer = new StringTokenizer(string, "\t\n\r-.");
        int count = stringTokenizer.countTokens();
        System.out.println(count);

        int indexI = count / 6;
        int indexJ = 6;
        Object[][] data = new Object[indexI][indexJ];
        String s = null;
        for (int i = 0; i < indexI; i++) {
            for (int j = 0; j < indexJ; j++) {
                data[i][j] = stringTokenizer.nextElement().toString();
            }
        }
        Object[] vmid = new Object[indexI];

        final DefaultTableModel defaultTableModel = new TableModel();
        String[] headings = {"", "", "VMID", "DATE", "TIME", ""};
        defaultTableModel.setDataVector(data, headings);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JTable tempTable = clientFrame.getTableBackups();
                tempTable.setModel(defaultTableModel);
            }
        });
    }

    //Заполнение таблицы существующих задач на удаленном сервере
    private void fillTableTasks(String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, "\t\n\r|");

        int count = stringTokenizer.countTokens();
        int indexI = count / 7;
        int indexJ = 7;
        Object[][] data = new Object[indexI][indexJ];
        for (int i = 0; i < indexI; i++) {
            for (int j = 0; j < indexJ; j++) {
                data[i][j] = stringTokenizer.nextElement().toString();
            }
        }
        final DefaultTableModel model = new TableModel();
        String[] headings = {"ID", "Время создания", "Время выполнения", "Тип задачи", "VMID", "Путь", "Периодичность"};
        model.setDataVector(data, headings);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JTable tempTable = clientFrame.getTableTasks();
                tempTable.setModel(model);
            }
        });

    }
}

//Модель таблицы с запрещенным свойством изменения размеров столбцов
class TableModel extends javax.swing.table.DefaultTableModel {
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}
