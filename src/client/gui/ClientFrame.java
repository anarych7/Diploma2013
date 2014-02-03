package client.gui;

import modules.connector.ClientConnector;
import modules.object.NetObject;
import modules.object.Pack;
import modules.object.Task;
import modules.service.Executor;
import modules.service.Sender;
import modules.service.Shower;
import modules.service.XMLParser;
import org.freixas.jcalendar.DateEvent;
import org.freixas.jcalendar.DateListener;
import org.freixas.jcalendar.JCalendar;
import org.freixas.jcalendar.JCalendarCombo;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientFrame {
    private static LinkedBlockingQueue<Pack> commandQueue;
    private static LinkedBlockingQueue<Pack> dataQueue;
    private static Executor executor;
    private static Shower shower;
    private static Thread showerClientThread;
    private static Thread executorClientThread;
    private final String PATH_TO_SERVER_LIST = "serverList.xml";
    private final JButton buttonVzDump = new JButton("Сделать резервную копию");
    private final JButton buttonVzRestore = new JButton("Восстановить из резервной копии");
    private ClientConnector clientConnector;
    private JPanel rootPanel;
    private JTextField textFieldIp;
    private JTextField textFieldPort;
    private JButton buttonConnect;
    private JButton buttonGetListBackups;
    private JLabel jLabelCurrentStatus;
    private JButton buttonGetServerList;
    private JButton buttonDisconnect;
    private JButton buttonGetListVM;
    private JTextField textFieldRunCommand;
    private JButton buttonRun;
    private JTable tableVMs;
    private JTable tableServers;
    private JTable tableBackups;
    private JTextArea textAreaConsole;
    private JButton buttonSendTask;
    private JButton buttonGetTasks;
    private JTable tableTasks;
    private JTabbedPane mainTabbedPane;
    private JPanel connectingTab;
    private JPanel taskingTab;
    private JPanel managePanel;
    private JScrollPane taskScrollPanel;
    private JPanel consoleTab;
    private JPanel consolePanel;
    private JScrollPane consoleScrollPanel;
    private JPanel runPanel;
    private JPanel createTaskPanel;
    private JButton buttonDateTaskExecute;
    private JLabel labelDateTaskExecute;
    private JLabel labelTaskType;
    private JComboBox comboBoxTaskType;
    private JComboBox comboBoxVMID;
    private JLabel labelVMID;
    private JTextField textFieldArchive;
    private JLabel labelArchive;
    private JComboBox comboBoxPeriodType;
    private JLabel labelPeriodType;
    private JPanel taskPanel;
    private JPanel manageTab;
    private JPanel vmPanel;
    private JScrollPane vmScrollPanel;
    private JPanel backupPanel;
    private JScrollPane backupScrollPanel;
    private JPanel statusPanel;
    private JPanel serverPanel;
    private JScrollPane serversScrollPanel;
    private JPanel connectPanel;
    private JLabel jLabelIp;
    private JLabel jLabelPort;
    private JPopupMenu pm;
    private JCalendar calendarTaskExecute;
    private JFrame dateChooserFrame;
    private Date dateTaskExecute;

    public ClientFrame() {
        dataQueue = new LinkedBlockingQueue<Pack>();
        commandQueue = new LinkedBlockingQueue<Pack>();
        shower = new Shower(dataQueue, this);
        executor = new Executor(commandQueue);
        showerClientThread = new Thread(shower, "showerClientThread");
        executorClientThread = new Thread(executor, "executorClientThread");
        calendarTaskExecute = new JCalendar(JCalendarCombo.DISPLAY_DATE | JCalendarCombo.DISPLAY_TIME, true);
        buttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip = textFieldIp.getText();
                if (checkString(textFieldPort.getText())) {
                    try {
                        int port = Integer.parseInt(textFieldPort.getText());
                        clientConnector = new ClientConnector(ip, port);
                        clientConnector.switchOn();
                        jLabelCurrentStatus.setText("Connected");
                        mainTabbedPane.setEnabledAt(1, true);
                        mainTabbedPane.setEnabledAt(2, true);
                        buttonConnect.setEnabled(false);
                        buttonDisconnect.setEnabled(true);
                    } catch (IOException e1) {
                        jLabelCurrentStatus.setText("Cannot connect to server");
                    }
                } else {
                    jLabelCurrentStatus.setText("Check data");
                }
            }
        });
        buttonDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientConnector.switchOff();
                jLabelCurrentStatus.setText("Disconnected");

                mainTabbedPane.setEnabledAt(1, false);
                mainTabbedPane.setEnabledAt(2, false);
                mainTabbedPane.setEnabledAt(3, false);
                buttonConnect.setEnabled(true);
                buttonDisconnect.setEnabled(false);

                tableVMs.setModel(new DefaultTableModel());
                tableBackups.setModel(new DefaultTableModel());
                tableTasks.setModel(new DefaultTableModel());
                textFieldRunCommand.setText("");
                textAreaConsole.setText("");
            }
        });
        buttonGetServerList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String listFromXML = null;
                try {
                    listFromXML = new XMLParser().getServerList(new File(PATH_TO_SERVER_LIST));
                } catch (ParserConfigurationException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (SAXException e1) {
                    e1.printStackTrace();
                }
                try {
                    dataQueue.put(
                            new Pack(null, new NetObject(
                                    NetObject.CommandType.DATA,
                                    NetObject.AnswerType.LIST_FROM_XML,
                                    "",
                                    listFromXML,
                                    ""
                            ))
                    );
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });
        tableServers.getTableHeader().setReorderingAllowed(false);
        tableServers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    int indexRow = tableServers.getSelectedRow();
                    int indexColumn = tableServers.getSelectedColumn();
                    if (indexColumn == 0) {
                        textFieldIp.setText(tableServers.getValueAt(indexRow, indexColumn).toString());
                        textFieldPort.setText(tableServers.getValueAt(indexRow, indexColumn + 1).toString());
                    }
                    if (indexColumn == 1) {
                        textFieldIp.setText(tableServers.getValueAt(indexRow, indexColumn - 1).toString());
                        textFieldPort.setText(tableServers.getValueAt(indexRow, indexColumn).toString());
                    }
                }
            }
        });
        tableVMs.getTableHeader().setReorderingAllowed(false);
        tableVMs.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if ((e.getClickCount() == 1) & (e.getButton() == MouseEvent.BUTTON3)) {
                    Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen(mouseLocation, tableVMs);
                    pm = new JPopupMenu();
                    pm.add(buttonVzDump);
                    pm.show(tableVMs, mouseLocation.x, mouseLocation.y);
                }
            }
        });
        tableBackups.getTableHeader().setReorderingAllowed(false);
        tableBackups.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if ((e.getClickCount() == 1) & (e.getButton() == MouseEvent.BUTTON3)) {
                    Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen(mouseLocation, tableVMs);
                    pm = new JPopupMenu();
                    pm.add(buttonVzRestore);
                    pm.show(tableVMs, mouseLocation.x, mouseLocation.y);
                }
            }
        });
        tableTasks.getTableHeader().setReorderingAllowed(false);
        buttonGetListVM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NetObject netObject = new NetObject(
                        NetObject.CommandType.SYSTEM,
                        NetObject.AnswerType.QM_LIST,
                        "qm list",
                        "",
                        ""
                );
                new Sender().send(clientConnector.getObjectOutputStream(), netObject);
            }
        });
        buttonRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NetObject netObject = new NetObject(
                        NetObject.CommandType.SYSTEM,
                        NetObject.AnswerType.GENERAL,
                        textFieldRunCommand.getText(),
                        "",
                        ""
                );
                new Sender().send(clientConnector.getObjectOutputStream(), netObject);
            }
        });
        buttonVzDump.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String vmid = tableVMs.getModel().getValueAt(tableVMs.getSelectedRow(), 0).toString();
                NetObject netObject = new NetObject(
                        NetObject.CommandType.SYSTEM,
                        NetObject.AnswerType.VZDUMP,
                        "vzdump " + vmid,
                        "",
                        ""
                );
                new Sender().send(clientConnector.getObjectOutputStream(), netObject);
                pm.setVisible(false);
            }
        });
        buttonVzRestore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableBackups.getSelectedRow();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(tableBackups.getModel().getValueAt(selectedRow, 0));
                for (int index = 1; index < tableBackups.getModel().getColumnCount() - 1; index++) {
                    stringBuilder.append("-");
                    stringBuilder.append(tableBackups.getModel().getValueAt(selectedRow, index));
                }
                stringBuilder.append(".");
                stringBuilder.append(
                        tableBackups.getModel().getValueAt(selectedRow, tableBackups.getModel().getColumnCount() - 1)
                );
                String archive = stringBuilder.toString();
                String vmid = tableBackups.getModel().getValueAt(selectedRow, 2).toString();
                NetObject netObject = new NetObject(
                        NetObject.CommandType.SYSTEM,
                        NetObject.AnswerType.VZRESTORE,
                        "vzrestore /var/lib/vz/dump/" + archive + " " + vmid,
                        null,
                        null
                );
                new Sender().send(clientConnector.getObjectOutputStream(), netObject);
                pm.setVisible(false);
            }
        });
        buttonGetListBackups.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NetObject netObject = new NetObject(
                        NetObject.CommandType.SYSTEM,
                        NetObject.AnswerType.DUMP_LIST,
                        "ls /var/lib/vz/dump/",
                        null,
                        null
                );
                new Sender().send(clientConnector.getObjectOutputStream(), netObject);
            }
        });
        buttonSendTask.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Task.TaskType taskType = null;
                switch (comboBoxTaskType.getSelectedIndex()) {
                    case 0:
                        taskType = Task.TaskType.VZDUMP;
                        break;
                    case 1:
                        taskType = Task.TaskType.VZRESTORE;
                        break;
                }
                String vmid = comboBoxVMID.getSelectedItem().toString();
                String archive;
                boolean scenario_one = comboBoxTaskType.getSelectedIndex() == 0 & textFieldArchive.getText() != "";
                boolean scenario_two = comboBoxTaskType.getSelectedIndex() == 1;
                if (scenario_one | scenario_two) {
                    archive = textFieldArchive.getText();
                    Task.PeriodType periodType = null;
                    switch (comboBoxPeriodType.getSelectedIndex()) {
                        case 0:
                            periodType = Task.PeriodType.ONCE;
                            break;
                        case 1:
                            periodType = Task.PeriodType.EVERY_DAY;
                            break;
                        case 2:
                            periodType = Task.PeriodType.EVERY_WEEK;
                            break;
                        case 3:
                            periodType = Task.PeriodType.EVERY_MONTH;
                            break;
                    }
                    NetObject netObject = new NetObject(
                            NetObject.CommandType.ADD_TASK,
                            NetObject.AnswerType.GENERAL,
                            new Task(
                                    Calendar.getInstance().getTime(),
                                    dateTaskExecute,
                                    taskType,
                                    vmid,
                                    archive,
                                    periodType
                            ),
                            "",
                            ""
                    );
                    new Sender().send(clientConnector.getObjectOutputStream(), netObject);
                }
            }
        });
        buttonGetTasks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NetObject netObject = new NetObject(
                        NetObject.CommandType.SHOW_TASK,
                        NetObject.AnswerType.TASK_LIST,
                        ":::", ":::", ":::");
                new Sender().send(clientConnector.getObjectOutputStream(), netObject);
            }
        });
        dateTaskExecute = calendarTaskExecute.getDate();
        buttonDateTaskExecute.setText(dateTaskExecute.toString());
        buttonDateTaskExecute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dateChooserFrame = new JFrame();
                calendarTaskExecute.addDateListener(new DateListener() {
                    @Override
                    public void dateChanged(DateEvent e) {
                        dateTaskExecute = calendarTaskExecute.getDate();
                        buttonDateTaskExecute.setText(dateTaskExecute.toString());
                        System.out.println(dateTaskExecute.toString());
                    }
                });
                dateChooserFrame.add(calendarTaskExecute);
                dateChooserFrame.pack();
                dateChooserFrame.setVisible(true);
            }
        });
        comboBoxTaskType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (comboBoxTaskType.getSelectedItem() == "VZRESTORE") {
                    textFieldArchive.setEnabled(true);
                } else
                    textFieldArchive.setEnabled(false);
            }
        });
        buttonConnect.setEnabled(true);
        buttonDisconnect.setEnabled(false);
        buttonGetServerList.setEnabled(true);
        mainTabbedPane.setEnabledAt(1, false);
        mainTabbedPane.setEnabledAt(2, false);
        mainTabbedPane.setEnabledAt(3, false);
        jLabelCurrentStatus.setText("Ready");
        showerClientThread.start();
        executorClientThread.start();
        jLabelCurrentStatus.setText("Threads start");
    }

    public static LinkedBlockingQueue<Pack> getCommandQueue() {
        return commandQueue;
    }

    public static LinkedBlockingQueue<Pack> getDataQueue() {
        return dataQueue;
    }

    private static boolean checkString(String string) {
        if (string == null || string.length() == 0) return false;
        int i = 0;
        if (string.charAt(0) == '-') {
            if (string.length() == 1) {
                return false;
            }
            i = 1;
        }
        char c;
        for (; i < string.length(); i++) {
            c = string.charAt(i);
            if (!(c >= '0' && c <= '9')) {
                return false;
            }
        }
        return true;
    }

    public JComboBox getComboBoxVMID() {
        return comboBoxVMID;
    }

    public JTable getTableBackups() {
        return tableBackups;
    }

    public JTextArea getTextAreaConsole() {
        return textAreaConsole;
    }

    public JTable getTableServers() {
        return tableServers;
    }

    public JTable getTableVMs() {
        return tableVMs;
    }

    public JTable getTableTasks() {
        return tableTasks;
    }

    public void init() {
        JFrame frame = new JFrame("ReplicationClient");
        frame.setContentPane(this.rootPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        SwingUtilities.updateComponentTreeUI(frame);
        Double dw = Toolkit.getDefaultToolkit().getScreenSize().getWidth() / (1.7);
        Double dh = Toolkit.getDefaultToolkit().getScreenSize().getHeight() / (1.4);
        frame.setSize(dw.intValue(), dh.intValue());
        frame.setVisible(true);
    }
}
/*
    mainTabbedPane.setEnabledAt(3, true);
    buttonGetListBackups.setEnabled(false);
    textFieldRunCommand.setEnabled(false);
    buttonRun.setEnabled(false);
    buttonGetListVM.setEnabled(false);
    tableVMs.setEnabled(false);
    tableBackups.setEnabled(false);
    buttonDateTaskExecute.setEnabled(false);
    comboBoxTaskType.setEnabled(false);
    comboBoxVMID.setEnabled(false);
    textFieldArchive.setEnabled(false);
    comboBoxPeriodType.setEnabled(false);
    buttonSendTask.setEnabled(false);
    tableTasks.setEnabled(false);
    buttonGetTasks.setEnabled(false);
    textFieldArchive.setEnabled(false);
    textAreaConsole.setEnabled(false);
    textAreaConsole.setEditable(false);
    buttonGetListBackups.setEnabled(false);
    textFieldRunCommand.setEnabled(false);
    buttonRun.setEnabled(false);
    buttonGetListVM.setEnabled(false);
    tableVMs.setEnabled(false);
    tableBackups.setEnabled(false);
    buttonDateTaskExecute.setEnabled(false);
    comboBoxTaskType.setEnabled(false);
    comboBoxVMID.setEnabled(false);
    textFieldArchive.setEnabled(false);
    comboBoxPeriodType.setEnabled(false);
    buttonSendTask.setEnabled(false);
    tableTasks.setEnabled(false);
    buttonGetTasks.setEnabled(false);
    textFieldArchive.setEnabled(false);
    textAreaConsole.setEnabled(false);
    buttonGetListBackups.setEnabled(true);
    textFieldRunCommand.setEnabled(true);
    buttonRun.setEnabled(true);
    buttonGetListVM.setEnabled(true);
    tableVMs.setEnabled(true);
    buttonDateTaskExecute.setEnabled(true);
    comboBoxTaskType.setEnabled(true);
    comboBoxVMID.setEnabled(true);
    comboBoxPeriodType.setEnabled(true);
    buttonSendTask.setEnabled(true);
    tableTasks.setEnabled(true);
    buttonGetTasks.setEnabled(true);
    tableBackups.setEnabled(true);
    textAreaConsole.setEnabled(true);
*/
