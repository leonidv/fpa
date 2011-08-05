package ru.spb.leonidv.flashpresentation.presentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Описывает презентацию как упорядоченный список информации об изображении. Под элементами
 * подразумевается объекты класса ImageInfo.
 * 
 * @author Leonid Vygovsky
 * @see ImageInfo
 */
public class Presentation {
    // Список расширений поддерживаемых изображений
    private static final String[] IMAGE_EXTENSIONS = { ".jpg", ".jpeg", ".png", "gif" };

    // Имя файла с описанием презентации в формате XML
    private static final String PRESENTATION_XML = "gallery.xml";

    // Фильтр файлов-картинок.
    FilenameFilter imagesFilter = new FilenameFilter() {

        public boolean accept(File dir, String name) {
            for (String ext : IMAGE_EXTENSIONS) {
                if (name.toLowerCase().endsWith(ext)) {
                    return true;
                }
            }
            return false;
        }

    };

    // Упорядоченный список с информацией об изображении
    private List<ImageInfo> data = new ArrayList<ImageInfo>();

    // Обработчик событий
    private PresentationEvent eventsHandler = new DefalutEvents();

    // Путь к данным презентации
    private File path;

    // Сервер
    private String server = "";

    // Описание презентации
    private String text = "";

    /**
     * Создает презентацию из картинок, находящехся в определенном каталоге. Под картинками
     * подразумеваются файлы с расширениями JPG и PNG.
     * 
     * @param path
     *            путь к каталогу, из которого берутся картинки.
     */
    public void loadFromFolder(File path) {
        this.path = path;
        /*
         * Перебираем каталог, в котором нужно найти файл. После этого добавляем в коллекцию все
         * файлы, которые имеют нужное нам расширение.
         */
        String[] files = path.list(imagesFilter);

        data.clear();

        eventsHandler.startLoading(files.length);

        for (int i = 0; i < files.length; i++) {
            String s = files[i];
            try {
                ImageInfo imageInfo = new ImageInfo(path.getPath() + File.separator + s);
                data.add(imageInfo);

                eventsHandler.imageLoaded(i, imageInfo);

            } catch (IOException ex) {
                System.out.println("Ошибка при попытке обработать файл " + s);
                ex.printStackTrace();
            }
        }

    }

    /**
     * Возвращаем элемент по его индексу.
     * 
     * @param index
     *            порядковый номер информации.
     * @return информацию об изображении, номер которой передан в качестве параметра
     */
    public ImageInfo getImageInfo(int index) {
        return data.get(index);
    }

    /**
     * Удаляет элемент из презентации.
     * 
     * @param index
     *            порядковый номер удаляемой из списка информации об изображении
     */
    public void remove(int index) {
        data.remove(index);
        eventsHandler.remove(index);
    }

    /**
     * Перемещает элемент на новое место.
     * 
     * @param source
     *            индекс элемента, который нужно переместить (откуда)
     * @param dest
     *            новый индекс элемента (куда)
     */
    public void move(int source, int dest) {
        ImageInfo temp = data.get(dest);
        data.set(dest, data.get(source));
        data.set(source, temp);

        eventsHandler.move(source, dest);
    }

    /**
     * Добавляет элемент в презентацию.
     * 
     * @param imageInfo
     *            добавляемая в презентацию информация об изображении.
     */
    public void add(ImageInfo imageInfo) {
        data.add(imageInfo);
    }

    /**
     * Возвращает количество элементов в презентации.
     * 
     * @return количество элементов в презентации.
     */
    public int size() {
        return data.size();
    }

    /**
     * @param eventsHandler
     *            the eventsHandler to set
     * @uml.property name="eventsHandler"
     */
    public void setEventsHandler(PresentationEvent eventsHandler) {
        this.eventsHandler = eventsHandler;
    }

    public boolean saveAsXML() {
        try {
            Document document = createXMLDocument();
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            t.setOutputProperty(OutputKeys.STANDALONE, "yes");
            t.setOutputProperty(OutputKeys.VERSION, "1.0");
            t.setOutputProperty(OutputKeys.INDENT, "yes");

            StreamResult output = new StreamResult(new FileOutputStream(getPresentationXMLFile()));
            t.transform(new DOMSource(document), output);

        } catch (ParserConfigurationException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (DOMException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (TransformerConfigurationException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (TransformerFactoryConfigurationError e) {
            System.out.println(e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return true;
    }

    private File getPresentationXMLFile() {
        return new File(path.getPath() + File.separatorChar + PRESENTATION_XML);
    }

    /**
     * @return
     * @throws ParserConfigurationException
     * @throws DOMException
     */
    private Document createXMLDocument() throws ParserConfigurationException, DOMException {
        Document document;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        builder = factory.newDocumentBuilder();
        document = builder.newDocument();

        Element gallery = document.createElement("gallery");
        gallery.setAttribute("editor", "FlashPresentation Anasas");

        gallery.appendChild(getPropertyXMLElement(document, "server", server));
        gallery.appendChild(getPropertyXMLElement(document, "text", text));

        for (ImageInfo imageInfo : data) {
            gallery.appendChild(getPhotoXMLElement(document, imageInfo));
        }

        document.appendChild(gallery);

        return document;
    }

    /**
     * Создает элемент описания одного изображения.
     * 
     * @param document
     *            Документ, узел которого необходимо создать
     * @param imageInfo
     *            Информация об изображение
     * @return узел с описанием изображения.
     * 
     * @throws DOMException
     */
    private Element getPhotoXMLElement(Document document, ImageInfo imageInfo) throws DOMException {
        Element photo = document.createElement("photo");

        photo.appendChild(getPropertyXMLElement(document, "file", imageInfo.getName()));

        String temp = String.valueOf(imageInfo.getWidth());
        photo.appendChild(getPropertyXMLElement(document, "width", temp));

        temp = String.valueOf(imageInfo.getHeight());
        photo.appendChild(getPropertyXMLElement(document, "height", temp));
        return photo;
    }

    /**
     * Создает элемент текстового вида <code>&ltname&gt value &lt/name&gt</code>
     * 
     * @param document
     *            Документ, узел которого будет создаваться
     * @param name
     *            Имя узла
     * @param value
     *            Значение, записываемое в узел
     * @return
     * @throws DOMException
     */
    private Element getPropertyXMLElement(Document document, String name, String value)
            throws DOMException {
        Element property;
        property = document.createElement(name);
        property.appendChild(document.createTextNode(value));
        return property;
    }

    /**
     * @param server
     *            the server to set
     * @uml.property name="server"
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @param text
     *            the text to set
     * @uml.property name="text"
     */
    public void setText(String text) {
        this.text = text;
    }
}

/**
 * Класс реализует шаблон "Нулевой объект" для обработки событий презентации по умолчанию.
 * 
 * @author Leonid
 * 
 */
class DefalutEvents implements PresentationEvent {

    public void endLoading() {
    }

    public void imageLoaded(int index, ImageInfo imageInfo) {
    }

    public void move(int source, int dest) {
    }

    public void startLoading(int count) {
    }

    public void remove(int index) {
    }
}
