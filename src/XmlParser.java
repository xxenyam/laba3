import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.w3c.dom.Element;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class XmlParser {

    static Set<String> ethnicities = new HashSet<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("Виберіть дію:");
            System.out.println("1. Проаналізуйте та відобразіть документ XML");
            System.out.println("2. Перевірте XML-документ на відповідність схемі XSD");
            System.out.println("3. Відобразити етнічні групи, присутні в документі");
            System.out.println("4. Відображення найпопулярніших імен певної етнічної групи");
            System.out.println("5. Вихід");
            System.out.print("Ваш вибір: ");
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    parseAndDisplayXmlDocument();
                    break;
                case 2:
                    validateXmlAgainstXsd();
                    break;
                case 3:
                    displayEthnicities();
                    break;
                case 4:
                    displayTopPopularNames();
                    break;
                case 5:
                    System.out.println("Вихід...");
                    break;
                default:
                    System.out.println("Невірний вибір. Будь ласка, введіть число від 1 до 5.");
            }
        } while (choice != 5);
        scanner.close();
    }

    static void parseAndDisplayXmlDocument() {
        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {
                boolean bTag = false;
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    System.out.println("Початковий елемент: " + qName);
                    bTag = true;
                }
                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    System.out.println("Кінцевий елемент: " + qName);
                    bTag = false;
                }
                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    if (bTag) {
                        System.out.println("Текст: " + new String(ch, start, length));
                    }
                }
            };

            saxParser.parse(new File("C:\\Users\\Irina\\cross\\laba3\\Popular_Baby_Names_NY.xml"), handler);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    static void validateXmlAgainstXsd() {
        try {

            File xsdFile = new File("C:\\Users\\Irina\\cross\\laba3\\Popular_Baby_Names_NY.xsd");
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(xsdFile);

            System.out.println("XML документ відповідає XSD схеме.");
        } catch (SAXException e) {

            e.printStackTrace();
        }
    }


    static void displayEthnicities() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParserEthnicity = factory.newSAXParser();

            DefaultHandler handlerEthnicity = new DefaultHandler() {
                boolean inEthnicityTag = false;

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    if (qName.equalsIgnoreCase("ethcty")) {
                        inEthnicityTag = true;
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    if (inEthnicityTag) {
                        String ethnicity = new String(ch, start, length).trim();
                        if (!ethnicity.isEmpty()) {
                            ethnicities.add(ethnicity);
                        }
                    }
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    if (qName.equalsIgnoreCase("ethcty")) {
                        inEthnicityTag = false;
                    }
                }
            };

            saxParserEthnicity.parse(new File("C:\\Users\\Irina\\cross\\laba3\\Popular_Baby_Names_NY.xml"), handlerEthnicity);

            System.out.println("Етнічні групи:");
            for (String ethnicity : ethnicities) {
                System.out.println(ethnicity);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }


    static void displayTopPopularNames() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введіть етнічну групу: ");
            String ethnicity = scanner.nextLine();

            System.out.print("Введіть гендер (MALE or FEMALE): ");
            String gender = scanner.nextLine();

            List<BabyName> babyNames = new ArrayList<>();

            File inputFile = new File("C:\\Users\\Irina\\cross\\laba3\\Popular_Baby_Names_NY.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("row");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) nNode;
                    String ethnicityNode = element.getElementsByTagName("ethcty").item(0).getTextContent();
                    String genderNode = element.getElementsByTagName("gndr").item(0).getTextContent();
                    if (ethnicityNode.equalsIgnoreCase(ethnicity) && genderNode.equalsIgnoreCase(gender)) {
                        String name = element.getElementsByTagName("nm").item(0).getTextContent();
                        int count = Integer.parseInt(element.getElementsByTagName("cnt").item(0).getTextContent());
                        int rating = Integer.parseInt(element.getElementsByTagName("rnk").item(0).getTextContent());
                        babyNames.add(new BabyName(name, gender, count, rating, ethnicityNode));
                    }
                }
            }

            // Теперь мы должны объединить одинаковые имена, чтобы сложить их count
            List<BabyName> mergedNames = new ArrayList<>();
            for (BabyName name : babyNames) {
                boolean found = false;
                for (BabyName merged : mergedNames) {
                    if (merged.getName().equalsIgnoreCase(name.getName())) {
                        merged.setCount(merged.getCount() + name.getCount());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    mergedNames.add(name);
                }
            }

            Collections.sort(mergedNames);

            System.out.println("Топ 10 популярних імен в " + ethnicity + " для " + gender + ":");
            for (int i = 0; i < Math.min(10, mergedNames.size()); i++) {
                BabyName babyName = mergedNames.get(i);
                System.out.println("Ім'я: " + babyName.getName() + ", Гендер: " + babyName.getGender() + ", Кількість: " + babyName.getCount() + ", Рейтинг: " + babyName.getRating());
            }
        } catch (ParserConfigurationException | SAXException | IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    static class BabyName implements Comparable<BabyName> {
        private String name;
        private String gender;
        private int count;
        private int rating;
        private String ethnicity;

        public BabyName(String name, String gender, int count, int rating, String ethnicity) {
            this.name = name;
            this.gender = gender;
            this.count = count;
            this.rating = rating;
            this.ethnicity = ethnicity;
        }

        public String getName() {
            return name;
        }

        public String getGender() {
            return gender;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getRating() {
            return rating;
        }

        public String getEthnicity() {
            return ethnicity;
        }

        @Override
        public int compareTo(BabyName o) {
            return Integer.compare(o.rating, this.rating);
        }
    }

}
