package modules.service;

import modules.object.Task;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public class XMLParser {

    public XMLParser() {
    }

    //Метод получения списка серверов
    public String getServerList(File xmlFile) throws ParserConfigurationException, IOException, SAXException {

        //Подготовка к разбору файла
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(xmlFile);
        document.getDocumentElement().normalize();

        //Получение структуры файла и генерация списка серверов
        NodeList nodeList = document.getElementsByTagName(document.getDocumentElement().getChildNodes().item(1).getNodeName());
        StringBuilder stringBuilder = new StringBuilder();
        for (int tmp = 0; tmp < nodeList.getLength(); tmp++) {
            Node node = nodeList.item(tmp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                stringBuilder.append(element.getElementsByTagName("ip").item(0).getChildNodes().item(0).getNodeValue());
                stringBuilder.append(":");
                stringBuilder.append(element.getElementsByTagName("port").item(0).getChildNodes().item(0).getNodeValue());
                stringBuilder.append("|\n");
            }
        }
        return stringBuilder.toString();
    }

    //Метод генерации массива задач из файла
    public Task[] getTaskList(File xmlFile) throws ParserConfigurationException, IOException, SAXException {

        //Подготовка к разбору файла
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(xmlFile);
        document.getDocumentElement().normalize();

        //Получение структуры файла и добавление каждого элемента в массив
        NodeList nodeList = document.getElementsByTagName(document.getDocumentElement().getChildNodes().item(0).getNodeName());
        Task[] taskList = new Task[nodeList.getLength()];
        for (int index = 0; index < nodeList.getLength(); index++) {
            Node node = nodeList.item(index);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                Date dtc = new Date(Long.parseLong(element.getElementsByTagName("dtc").item(0).getChildNodes().item(0).getNodeValue()));
                Date dte = new Date(Long.parseLong(element.getElementsByTagName("dte").item(0).getChildNodes().item(0).getNodeValue()));
                String tt = element.getElementsByTagName("tt").item(0).getChildNodes().item(0).getNodeValue();
                String vmid = element.getElementsByTagName("vmid").item(0).getChildNodes().item(0).getNodeValue();
                String archive = element.getElementsByTagName("archive").item(0).getChildNodes().item(0).getNodeValue();
                String pt = element.getElementsByTagName("pt").item(0).getChildNodes().item(0).getNodeValue();
                Task taskToArray = new Task(dtc, dte, Task.TaskType.valueOf(tt), vmid, archive, Task.PeriodType.valueOf(pt));
                taskToArray.setId(Integer.parseInt(document.getElementsByTagName("task").item(index).getAttributes().item(0).getNodeValue()));
                taskList[index] = taskToArray;
            }
        }
        return taskList;
    }

    //Метод добавления задачи в файл
    public void addTaskToTaskList(File xmlFile, Task newTask) throws ParserConfigurationException, IOException, SAXException, TransformerException {

        //Разбор полей объекта задачи
        Date dtc = newTask.dateTaskCreated;
        Date dte = newTask.dateTaskExecute;
        String tt = newTask.taskType.toString();
        String vmid = newTask.vmid;
        String archive = newTask.archive;
        String pt = newTask.periodType.toString();

        //Подготовка к разбору файла
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(xmlFile);
        document.getDocumentElement().normalize();

        //Получаем корневой элемент
        Node root = document.getFirstChild();
        NodeList nodeList = document.getElementsByTagName("task");

        //Child's корневого элемента
        Element elementTask = document.createElement("task");
        root.appendChild(elementTask);

        //Установка атрибута элемента task
        Attr attr = document.createAttribute("id");
        attr.setValue(String.valueOf(nodeList.getLength()));
        elementTask.setAttributeNode(attr);

        //Установка атрибута элемента dtc
        Element elementDTC = document.createElement("dtc");
        elementDTC.appendChild(document.createTextNode(String.valueOf(dtc.getTime())));
        elementTask.appendChild(elementDTC);

        //Установка атрибута элемента dte
        Element elementDTE = document.createElement("dte");
        elementDTE.appendChild(document.createTextNode(String.valueOf(dte.getTime())));
        elementTask.appendChild(elementDTE);

        //Установка атрибута элемента tt
        Element elementTT = document.createElement("tt");
        elementTT.appendChild(document.createTextNode(tt));
        elementTask.appendChild(elementTT);

        //Установка атрибута элемента vmid
        Element elementVMID = document.createElement("vmid");
        elementVMID.appendChild(document.createTextNode(vmid));
        elementTask.appendChild(elementVMID);

        //Установка атрибута элемента archive
        Element elementARCHIVE = document.createElement("archive");
        elementARCHIVE.appendChild(document.createTextNode(archive));
        elementTask.appendChild(elementARCHIVE);

        //Установка атрибута элемента pt
        Element elementPT = document.createElement("pt");
        elementPT.appendChild(document.createTextNode(pt));
        elementTask.appendChild(elementPT);

        //Запись изменений в файл
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(xmlFile);
        transformer.transform(domSource, streamResult);
    }

    //Метод удаления задачи из файла
    public void delTaskFromTaskList(File xmlFile, Task task) throws ParserConfigurationException, IOException, SAXException {

        //Подготовка к разбору файла
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(xmlFile);
        document.getDocumentElement().normalize();
    }

    //Метод обновления задачи в файле
    public void updateTaskInTaskList(File xmlFile, Task oldTask, Task newTask) throws ParserConfigurationException, IOException, SAXException, TransformerException {

        //Подготовка к разбору файла
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(xmlFile);
        document.getDocumentElement().normalize();

        //Получение корневого элемента
        Node root = document.getFirstChild();
        NodeList nodeList = document.getElementsByTagName("task");

        //Поиск элемента task для оновления
        for (int index = 0; index < nodeList.getLength(); index++) {
            Node task = document.getElementsByTagName("task").item(index);
            Element element = (Element) task;
            int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getChildNodes().item(0).getNodeValue());
            if (id == oldTask.getId()) {
                //Модифицируем атрибут элемента
                NamedNodeMap namedNodeMap = task.getAttributes();
                Node nodeAttr = namedNodeMap.getNamedItem("dte");
                nodeAttr.setTextContent(String.valueOf(newTask.dateTaskExecute.getTime()));
            }
        }

        //Запись изменений в файл
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(xmlFile);
        transformer.transform(domSource, streamResult);
    }
}
