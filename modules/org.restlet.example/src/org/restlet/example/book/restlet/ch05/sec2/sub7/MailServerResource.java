package org.restlet.example.book.restlet.ch05.sec2.sub7;

import java.io.IOException;

import javax.xml.transform.OutputKeys;

import org.restlet.data.LocalReference;
import org.restlet.data.Reference;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.ext.xml.XsltRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Resource corresponding to a mail received or sent with the parent mail
 * account. Leverages XML Schema validation.
 */
public class MailServerResource extends ServerResource {

    @Override
    protected Representation get() throws ResourceException {
        XsltRepresentation result = null;

        try {
            // Create a new DOM representation
            DomRepresentation rmepMail = new DomRepresentation();
            rmepMail.setIndenting(true);

            // Populate the DOM document
            Document doc = rmepMail.getDocument();

            Node mailElt = doc.createElement("mail");
            doc.appendChild(mailElt);

            Node statusElt = doc.createElement("status");
            statusElt.setTextContent("received");
            mailElt.appendChild(statusElt);

            Node subjectElt = doc.createElement("subject");
            subjectElt.setTextContent("Message to self");
            mailElt.appendChild(subjectElt);

            Node contentElt = doc.createElement("content");
            contentElt.setTextContent("Doh!");
            mailElt.appendChild(contentElt);

            Node accountRefElt = doc.createElement("accountRef");
            accountRefElt.setTextContent(new Reference(getReference(), "..")
                    .getTargetRef().toString());
            mailElt.appendChild(accountRefElt);

            // Transform to another XML format using XSLT
            Representation transformSheet = new ClientResource(LocalReference
                    .createClapReference(getClass().getPackage())
                    + "/Mail.xslt").get();
            result = new XsltRepresentation(rmepMail, transformSheet);
            result.getOutputProperties().put(OutputKeys.INDENT, "yes");
        } catch (IOException e) {
            throw new ResourceException(e);
        }

        return result;
    }

    @Override
    protected Representation put(Representation representation)
            throws ResourceException {
        DomRepresentation mailRep = new DomRepresentation(representation);

        // Retrieve the XML element using XPath expressions
        String subject = mailRep.getText("/email/head/subject");
        String content = mailRep.getText("/email/body");

        // Output the XML element values
        System.out.println("Subject: " + subject);
        System.out.println("Content: " + content);

        return null;
    }
}
