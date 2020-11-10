/*
 * ForceStub.java
 *
 * Copyright (c) 2011 Jay Lawson <jaylawson39 at yahoo.com>. All rights reserved.
 *
 * This file is part of MekHQ.
 *
 * MekHQ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MekHQ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MekHQ. If not, see <http://www.gnu.org/licenses/>.
 */
package mekhq.campaign.force;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.UUID;
import java.util.Vector;

import mekhq.icons.LayeredForceIcon;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import mekhq.MekHQ;
import mekhq.MekHqXmlUtil;
import mekhq.campaign.Campaign;
import mekhq.campaign.unit.Unit;

/**
 * this is a hierarchical object that represents forces from the TO&E using
 * strings rather than unit objects. This makes it static and thus usable to
 * keep track of forces involved in completed scenarios
 *
 * @author Jay Lawson <jaylawson39 at yahoo.com>
 */
public class ForceStub implements Serializable {
    private static final long serialVersionUID = -7283462987261602481L;

    // pathway to force icon
    private LayeredForceIcon forceIcon = new LayeredForceIcon();

    private String name;
    private Vector<ForceStub> subForces;
    private Vector<UnitStub> units;

    public ForceStub() {
        name = "";
        subForces = new Vector<>();
        units = new Vector<>();
    }

    public ForceStub(Force force, Campaign c) {
        name = force.getFullName();
        subForces = new Vector<>();
        units = new Vector<>();
        forceIcon = force.getForceIcon().clone();

        for (Force sub : force.getSubForces()) {
            ForceStub stub = new ForceStub(sub, c);
            //stub.setParentForce(this);
            subForces.add(stub);
        }
        for (UUID uid : force.getUnits()) {
            Unit u = c.getUnit(uid);
            if (null != u) {
                units.add(new UnitStub(u));
            }
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public Vector<Object> getAllChildren() {
        Vector<Object> children = new Vector<>();
        children.addAll(subForces);
        children.addAll(units);

        return children;
    }

    public LayeredForceIcon getForceIcon() {
        return forceIcon;
    }

    public void setForceIcon(LayeredForceIcon forceIcon) {
        this.forceIcon = forceIcon;
    }

    public void writeToXml(PrintWriter pw1, int indent) {
        MekHqXmlUtil.writeSimpleXMLOpenIndentedLine(pw1, indent++, "forceStub");
        MekHqXmlUtil.writeSimpleXmlTag(pw1, indent, "name", name);
        getForceIcon().writeToXML(pw1, indent);

        if (units.size() > 0) {
            MekHqXmlUtil.writeSimpleXMLOpenIndentedLine(pw1, indent++, "units");
            for (UnitStub ustub : units) {
                ustub.writeToXml(pw1, indent);
            }
            MekHqXmlUtil.writeSimpleXMLCloseIndentedLine(pw1, --indent, "units");
        }

        if (subForces.size() > 0) {
            MekHqXmlUtil.writeSimpleXMLOpenIndentedLine(pw1, indent++, "subforces");
            for (ForceStub sub : subForces) {
                sub.writeToXml(pw1, indent);
            }
            MekHqXmlUtil.writeSimpleXMLCloseIndentedLine(pw1, --indent, "subforces");
        }
        MekHqXmlUtil.writeSimpleXMLCloseIndentedLine(pw1, --indent, "forceStub");
    }

    public static ForceStub generateInstanceFromXML(Node wn) {
        ForceStub retVal = null;

        try {
            retVal = new ForceStub();
            NodeList nl = wn.getChildNodes();

            for (int x = 0; x < nl.getLength(); x++) {
                Node wn2 = nl.item(x);
                if (wn2.getNodeName().equalsIgnoreCase("name")) {
                    retVal.name = wn2.getTextContent();
                } else if (wn2.getNodeName().equalsIgnoreCase("iconCategory")) {
                    retVal.getForceIcon().setCategory(wn2.getTextContent().trim());
                } else if (wn2.getNodeName().equalsIgnoreCase("iconHashMap")) {
                    retVal.getForceIcon().processIconMapNodes(wn2);
                } else if (wn2.getNodeName().equalsIgnoreCase("iconFileName")) {
                    retVal.getForceIcon().setFilename(wn2.getTextContent().trim());
                } else if (wn2.getNodeName().equalsIgnoreCase("units")) {
                    NodeList nl2 = wn2.getChildNodes();
                    for (int y = 0; y < nl2.getLength(); y++) {
                        Node wn3 = nl2.item(y);
                        // If it's not an element node, we ignore it.
                        if (wn3.getNodeType() != Node.ELEMENT_NODE)
                            continue;

                        if (!wn3.getNodeName().equalsIgnoreCase("unitStub")) {
                            // Error condition of sorts!
                            // Errr, what should we do here?
                            MekHQ.getLogger().error("Unknown node type not loaded in ForceStub nodes: " + wn3.getNodeName());
                            continue;
                        }

                        retVal.units.add(UnitStub.generateInstanceFromXML(wn3));
                    }
                } else if (wn2.getNodeName().equalsIgnoreCase("subforces")) {
                    NodeList nl2 = wn2.getChildNodes();
                    for (int y = 0; y < nl2.getLength(); y++) {
                        Node wn3 = nl2.item(y);
                        // If it's not an element node, we ignore it.
                        if (wn3.getNodeType() != Node.ELEMENT_NODE)
                            continue;

                        if (!wn3.getNodeName().equalsIgnoreCase("forceStub")) {
                            // Error condition of sorts!
                            // Errr, what should we do here?
                            MekHQ.getLogger().error("Unknown node type not loaded in ForceStub nodes: " + wn3.getNodeName());
                            continue;
                        }

                        retVal.addSubForce(generateInstanceFromXML(wn3));
                    }
                }
            }
        } catch (Exception ex) {
            // Errrr, apparently either the class name was invalid...
            // Or the listed name doesn't exist.
            // Doh!
            MekHQ.getLogger().error(ex);
        }

        return retVal;
    }

    public void addSubForce(ForceStub sub) {
        subForces.add(sub);
    }
}
