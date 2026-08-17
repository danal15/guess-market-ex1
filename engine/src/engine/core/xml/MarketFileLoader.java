package engine.core.xml;

import engine.api.exception.InvalidMarketFileException;
import engine.core.Market;
import engine.model.CommissionType;
import engine.model.Event;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MarketFileLoader {

    private MarketFileLoader() {
    }

    public static Market load(String rawPath) {
        String path = stripQuotes(rawPath == null ? "" : rawPath.trim());
        if (path.isEmpty()) {
            throw new InvalidMarketFileException("No file path was entered.");
        }

        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            throw new InvalidMarketFileException("No file exists at: " + path);
        }
        if (!path.toLowerCase().endsWith(".xml")) {
            throw new InvalidMarketFileException("The file must have an .xml extension: " + path);
        }

        Document document;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(file);
        } catch (Exception e) {
            throw new InvalidMarketFileException("The file is not a well-formed XML file: " + e.getMessage());
        }

        Element root = document.getDocumentElement();
        if (root == null || !"Guess-Market".equals(root.getTagName())) {
            throw new InvalidMarketFileException("The root element must be <Guess-Market>.");
        }

        Element eventsElement = firstChildElement(root, "GM-events");
        if (eventsElement == null) {
            throw new InvalidMarketFileException("Missing required <GM-events> element.");
        }

        List<Element> eventElements = childElements(eventsElement, "GM-event");
        if (eventElements.isEmpty()) {
            throw new InvalidMarketFileException("The file contains no <GM-event> elements.");
        }

        Map<Integer, String> seenIds = new HashMap<>();
        List<Event> events = new ArrayList<>();

        for (Element eventElement : eventElements) {
            events.add(parseEvent(eventElement, seenIds));
        }

        return new Market(events);
    }

    private static Event parseEvent(Element eventElement, Map<Integer, String> seenIds) {
        String name = eventElement.getAttribute("name");
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidMarketFileException("An event is missing the required 'name' attribute.");
        }
        name = name.trim();

        int id = parseIntElement(eventElement, "id", name);
        String description = textOfChild(eventElement, "description", name);

        Element comisionEl = firstChildElement(eventElement, "comision");
        if (comisionEl == null) {
            throw new InvalidMarketFileException("Event '" + name + "' is missing the required <comision> element.");
        }
        int commissionPercent;
        try {
            commissionPercent = Integer.parseInt(comisionEl.getTextContent().trim());
        } catch (NumberFormatException e) {
            throw new InvalidMarketFileException("Event '" + name + "' has a non-numeric commission value.");
        }
        String typeRaw = comisionEl.getAttribute("type");
        CommissionType commissionType = CommissionType.fromXmlValue(typeRaw);
        if (commissionType == null) {
            throw new InvalidMarketFileException(
                    "Event '" + name + "' has an unknown commission type: '" + typeRaw + "'.");
        }
        if (commissionPercent < 0 || commissionPercent > 90) {
            throw new InvalidMarketFileException("Event '" + name + "' (id " + id + ") has commission "
                    + commissionPercent + " - allowed range is 0 to 90.");
        }

        if (seenIds.containsKey(id)) {
            throw new InvalidMarketFileException("Duplicate event id " + id + ": used by '"
                    + seenIds.get(id) + "' and '" + name + "'.");
        }
        seenIds.put(id, name);

        Element optionsEl = firstChildElement(eventElement, "GM-options");
        if (optionsEl == null) {
            throw new InvalidMarketFileException("Event '" + name + "' is missing the required <GM-options> element.");
        }
        List<Element> optionElements = childElements(optionsEl, "GM-option");
        if (optionElements.size() != 2) {
            throw new InvalidMarketFileException("Event '" + name + "' must have exactly 2 options, found "
                    + optionElements.size() + ".");
        }
        String option1 = optionElements.get(0).getTextContent().trim();
        String option2 = optionElements.get(1).getTextContent().trim();
        if (option1.isEmpty() || option2.isEmpty()) {
            throw new InvalidMarketFileException("Event '" + name + "' has an empty option name.");
        }

        Element methodEl = firstChildElement(eventElement, "GM-method");
        if (methodEl == null) {
            throw new InvalidMarketFileException("Event '" + name + "' is missing the required <GM-method> element.");
        }
        Element lmsrEl = firstChildElement(methodEl, "GM-LMSR");
        if (lmsrEl == null) {
            throw new InvalidMarketFileException(
                    "Event '" + name + "' does not define an LMSR trading method (required in Exercise 1).");
        }
        int b = parseIntElement(lmsrEl, "b", name);
        if (b <= 0) {
            throw new InvalidMarketFileException(
                    "Event '" + name + "' has a liquidity value b=" + b + " - it must be a positive integer.");
        }

        return new Event(id, name, description, commissionPercent, commissionType, b, option1, option2);
    }

    private static int parseIntElement(Element parent, String childTag, String eventName) {
        String text = textOfChild(parent, childTag, eventName);
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidMarketFileException(
                    "Event '" + eventName + "' has a non-numeric value for <" + childTag + ">: '" + text + "'.");
        }
    }

    private static String textOfChild(Element parent, String childTag, String eventName) {
        Element child = firstChildElement(parent, childTag);
        if (child == null) {
            throw new InvalidMarketFileException(
                    "Event '" + eventName + "' is missing the required <" + childTag + "> element.");
        }
        return child.getTextContent().trim();
    }

    private static Element firstChildElement(Element parent, String tag) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && tag.equals(node.getNodeName())) {
                return (Element) node;
            }
        }
        return null;
    }

    private static List<Element> childElements(Element parent, String tag) {
        List<Element> result = new ArrayList<>();
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && tag.equals(node.getNodeName())) {
                result.add((Element) node);
            }
        }
        return result;
    }

    private static String stripQuotes(String s) {
        if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1).trim();
        }
        return s;
    }
}
