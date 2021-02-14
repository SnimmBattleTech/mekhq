/*
 * Copyright (c) 2021 - The MegaMek Team. All Rights Reserved.
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
package mekhq.campaign.universe.generators.companyGeneration;

import mekhq.MekHQ;
import mekhq.MekHqXmlUtil;
import mekhq.campaign.Campaign;
import mekhq.campaign.personnel.Person;
import mekhq.campaign.universe.Faction;
import mekhq.campaign.universe.Factions;
import mekhq.campaign.universe.Planet;
import mekhq.campaign.universe.Systems;
import mekhq.campaign.universe.enums.CompanyGenerationType;
import mekhq.campaign.universe.enums.ForceNamingType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CompanyGenerationOptions implements Serializable {
    //region Variable Declarations
    private static final long serialVersionUID = 3034123423672457769L;

    // Base Information
    private CompanyGenerationType type;
    private Faction faction;  // Not fully understood/Implemented
    private boolean specifyStartingPlanet;
    private Planet startingPlanet;
    private boolean generateMercenaryCompanyCommandLance;
    private int companyCount;
    private int individualLanceCount;
    private int lancesPerCompany;
    private int lanceSize;

    // Personnel
    private Map<Integer, Integer> supportPersonnel;
    private boolean poolAssistants;
    private boolean generateCaptains;
    private boolean assignCompanyCommanderFlag;
    private boolean companyCommanderLanceOfficer;
    private boolean applyOfficerStatBonusToWorstSkill;
    private boolean assignBestOfficers;
    private boolean automaticallyAssignRanks;

    // Personnel Randomization
    private boolean randomizeOrigin;
    private boolean randomizeAroundCentralPlanet;
    private Planet centralPlanet; // Not Implemented
    private int originSearchRadius;
    private boolean extraRandomOrigin;
    private double originDistanceScale;

    // Units
    private boolean generateUnitsAsAttached;
    private boolean assignBestRollToUnitCommander;  // Not Implemented
    private boolean groupByWeight;  // Buggy - Annoyingly Implemented, might need to work on how I've done this one before I open
    private boolean keepOfficerRollsSeparate;
    private int starLeagueYear;
    private boolean assignTechsToUnits;  // Not Implemented

    // Unit
    private ForceNamingType forceNamingType;
    private boolean generateForceIcons; // Very Buggy

    // Spares
    private boolean generateMothballedSpareUnits;
    private int sparesPercentOfActiveUnits;
    private boolean generateSpareParts;  // Not Implemented
    private int startingArmourWeight;  // Not Implemented
    private boolean generateSpareAmmunition;  // Not Implemented
    private int numberReloadsPerWeapon;  // Not Implemented
    private boolean generateFractionalMachineGunAmmunition;  // Not Implemented

    // Contracts
    private boolean selectStartingContract;  // Not Implemented
    private boolean startCourseToContractPlanet;

    // Finances
    private int startingCash; // Not Implemented
    private boolean randomizeStartingCash;
    private int randomStartingCashDiceCount;
    private int minimumStartingFloat; // Not Implemented
    private boolean payForSetup; // Not Implemented
    private boolean startingLoan; // Not Implemented
    private boolean payForPersonnel;
    private boolean payForUnits;
    private boolean payForParts;
    private boolean payForArmour;
    private boolean payForAmmunition;
    //endregion Variable Declarations

    //region Constructors
    public CompanyGenerationOptions() {

    }

    public CompanyGenerationOptions(final CompanyGenerationType type, final Campaign campaign) {
        // Base Information
        setType(type);
        setFaction(campaign.getFaction());
        setSpecifyStartingPlanet(true);
        setStartingPlanet(Systems.getInstance().getSystems().getOrDefault(
                getFaction().getStartingPlanet(campaign.getLocalDate()),
                campaign.getSystemByName("Terra")).getPrimaryPlanet());
        setCompanyCount(1);
        setIndividualLanceCount(0);
        setGenerateMercenaryCompanyCommandLance(false);
        setLancesPerCompany(3);
        setLanceSize(4);

        // Personnel
        Map<Integer, Integer> supportPersonnel = new HashMap<>();
        if (getType().isWindchild()) {
            supportPersonnel.put(Person.T_MECH_TECH, 5);
            supportPersonnel.put(Person.T_MECHANIC, 1);
            supportPersonnel.put(Person.T_AERO_TECH, 1);
            supportPersonnel.put(Person.T_DOCTOR, 1);
            supportPersonnel.put(Person.T_ADMIN_COM, 1);
            supportPersonnel.put(Person.T_ADMIN_LOG, 1);
            supportPersonnel.put(Person.T_ADMIN_TRA, 1);
            supportPersonnel.put(Person.T_ADMIN_HR, 1);
        } else { // Defaults to AtB
            supportPersonnel.put(Person.T_MECH_TECH, 10);
            supportPersonnel.put(Person.T_DOCTOR, 1);
            supportPersonnel.put(Person.T_ADMIN_LOG, 1);
        }
        setSupportPersonnel(supportPersonnel);
        setPoolAssistants(true);
        setGenerateCaptains(type.isWindchild());
        setAssignCompanyCommanderFlag(true);
        setCompanyCommanderLanceOfficer(type.isWindchild());
        setApplyOfficerStatBonusToWorstSkill(type.isWindchild());
        setAssignBestOfficers(type.isWindchild());
        setAutomaticallyAssignRanks(true);

        // Personnel Randomization
        setRandomizeOrigin(true);
        setRandomizeAroundCentralPlanet(true);
        setCentralPlanet(campaign.getSystemByName("Terra").getPrimaryPlanet());
        setOriginSearchRadius(1000);
        setExtraRandomOrigin(false);
        setOriginDistanceScale(0.2);

        // Units
        setGenerateUnitsAsAttached(type.isAtB());
        setAssignBestRollToUnitCommander(type.isWindchild());
        setGroupByWeight(true);
        setKeepOfficerRollsSeparate(type.isAtB());
        setStarLeagueYear(2765);
        setAssignTechsToUnits(true);

        // Unit
        setForceNamingType(ForceNamingType.CCB_1943);
        setGenerateForceIcons(true);

        // Spares
        setGenerateMothballedSpareUnits(false);
        setSparesPercentOfActiveUnits(10);
        setGenerateSpareParts(type.isWindchild());
        setStartingArmourWeight(60);
        setGenerateSpareAmmunition(type.isWindchild());
        setNumberReloadsPerWeapon(4);
        setGenerateFractionalMachineGunAmmunition(true);

        // Contracts
        setSelectStartingContract(true);
        setStartCourseToContractPlanet(true);

        // Finances
        setStartingCash(0);
        setRandomizeStartingCash(type.isWindchild());
        setRandomStartingCashDiceCount(8);
        setMinimumStartingFloat(type.isWindchild() ? 3500000 : 0);
        setPayForSetup(type.isWindchild());
        setStartingLoan(type.isWindchild());
        setPayForPersonnel(type.isWindchild());
        setPayForUnits(type.isWindchild());
        setPayForParts(type.isWindchild());
        setPayForArmour(type.isWindchild());
        setPayForAmmunition(type.isWindchild());
    }
    //endregion Constructors

    //region Getters/Setters
    //region Base Information
    public CompanyGenerationType getType() {
        return type;
    }

    public void setType(final CompanyGenerationType type) {
        this.type = type;
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(final Faction faction) {
        this.faction = faction;
    }

    public boolean isSpecifyStartingPlanet() {
        return specifyStartingPlanet;
    }

    public void setSpecifyStartingPlanet(final boolean specifyStartingPlanet) {
        this.specifyStartingPlanet = specifyStartingPlanet;
    }

    public Planet getStartingPlanet() {
        return startingPlanet;
    }

    public void setStartingPlanet(final Planet startingPlanet) {
        this.startingPlanet = startingPlanet;
    }

    public boolean isGenerateMercenaryCompanyCommandLance() {
        return generateMercenaryCompanyCommandLance;
    }

    public void setGenerateMercenaryCompanyCommandLance(final boolean generateMercenaryCompanyCommandLance) {
        this.generateMercenaryCompanyCommandLance = generateMercenaryCompanyCommandLance;
    }

    public int getCompanyCount() {
        return companyCount;
    }

    public void setCompanyCount(final int companyCount) {
        this.companyCount = companyCount;
    }

    public int getIndividualLanceCount() {
        return individualLanceCount;
    }

    public void setIndividualLanceCount(final int individualLanceCount) {
        this.individualLanceCount = individualLanceCount;
    }

    public int getLanceSize() {
        return lanceSize;
    }

    public void setLanceSize(final int lanceSize) {
        this.lanceSize = lanceSize;
    }

    public int getLancesPerCompany() {
        return lancesPerCompany;
    }

    public void setLancesPerCompany(final int lancesPerCompany) {
        this.lancesPerCompany = lancesPerCompany;
    }
    //endregion Base Information

    //region Personnel
    public Map<Integer, Integer> getSupportPersonnel() {
        return supportPersonnel;
    }

    public void setSupportPersonnel(final Map<Integer, Integer> supportPersonnel) {
        this.supportPersonnel = supportPersonnel;
    }

    public boolean isPoolAssistants() {
        return poolAssistants;
    }

    public void setPoolAssistants(final boolean poolAssistants) {
        this.poolAssistants = poolAssistants;
    }

    public boolean isGenerateCaptains() {
        return generateCaptains;
    }

    public void setGenerateCaptains(final boolean generateCaptains) {
        this.generateCaptains = generateCaptains;
    }

    public boolean isAssignCompanyCommanderFlag() {
        return assignCompanyCommanderFlag;
    }

    public void setAssignCompanyCommanderFlag(final boolean assignCompanyCommanderFlag) {
        this.assignCompanyCommanderFlag = assignCompanyCommanderFlag;
    }

    public boolean isCompanyCommanderLanceOfficer() {
        return companyCommanderLanceOfficer;
    }

    public void setCompanyCommanderLanceOfficer(final boolean companyCommanderLanceOfficer) {
        this.companyCommanderLanceOfficer = companyCommanderLanceOfficer;
    }

    public boolean isApplyOfficerStatBonusToWorstSkill() {
        return applyOfficerStatBonusToWorstSkill;
    }

    public void setApplyOfficerStatBonusToWorstSkill(final boolean applyOfficerStatBonusToWorstSkill) {
        this.applyOfficerStatBonusToWorstSkill = applyOfficerStatBonusToWorstSkill;
    }

    public boolean isAssignBestOfficers() {
        return assignBestOfficers;
    }

    public void setAssignBestOfficers(final boolean assignBestOfficers) {
        this.assignBestOfficers = assignBestOfficers;
    }

    public boolean isAutomaticallyAssignRanks() {
        return automaticallyAssignRanks;
    }

    public void setAutomaticallyAssignRanks(final boolean automaticallyAssignRanks) {
        this.automaticallyAssignRanks = automaticallyAssignRanks;
    }
    //endregion Personnel

    //region Personnel Randomization
    public boolean isRandomizeOrigin() {
        return randomizeOrigin;
    }

    public void setRandomizeOrigin(final boolean randomizeOrigin) {
        this.randomizeOrigin = randomizeOrigin;
    }

    public boolean isRandomizeAroundCentralPlanet() {
        return randomizeAroundCentralPlanet;
    }

    public void setRandomizeAroundCentralPlanet(final boolean randomizeAroundCentralPlanet) {
        this.randomizeAroundCentralPlanet = randomizeAroundCentralPlanet;
    }

    public Planet getCentralPlanet() {
        return centralPlanet;
    }

    public void setCentralPlanet(final Planet centralPlanet) {
        this.centralPlanet = centralPlanet;
    }

    public int getOriginSearchRadius() {
        return originSearchRadius;
    }

    public void setOriginSearchRadius(final int originSearchRadius) {
        this.originSearchRadius = originSearchRadius;
    }

    public boolean isExtraRandomOrigin() {
        return extraRandomOrigin;
    }

    public void setExtraRandomOrigin(final boolean extraRandomOrigin) {
        this.extraRandomOrigin = extraRandomOrigin;
    }

    public double getOriginDistanceScale() {
        return originDistanceScale;
    }

    public void setOriginDistanceScale(final double originDistanceScale) {
        this.originDistanceScale = originDistanceScale;
    }
    //endregion Personnel Randomization

    //region Units
    public boolean isGenerateUnitsAsAttached() {
        return generateUnitsAsAttached;
    }

    public void setGenerateUnitsAsAttached(final boolean generateUnitsAsAttached) {
        this.generateUnitsAsAttached = generateUnitsAsAttached;
    }

    public boolean isAssignBestRollToUnitCommander() {
        return assignBestRollToUnitCommander;
    }

    public void setAssignBestRollToUnitCommander(final boolean assignBestRollToUnitCommander) {
        this.assignBestRollToUnitCommander = assignBestRollToUnitCommander;
    }

    public boolean isGroupByWeight() {
        return groupByWeight;
    }

    public void setGroupByWeight(final boolean groupByWeight) {
        this.groupByWeight = groupByWeight;
    }

    public boolean isKeepOfficerRollsSeparate() {
        return keepOfficerRollsSeparate;
    }

    public void setKeepOfficerRollsSeparate(final boolean keepOfficerRollsSeparate) {
        this.keepOfficerRollsSeparate = keepOfficerRollsSeparate;
    }

    public int getStarLeagueYear() {
        return starLeagueYear;
    }

    public void setStarLeagueYear(final int starLeagueYear) {
        this.starLeagueYear = starLeagueYear;
    }

    public boolean isAssignTechsToUnits() {
        return assignTechsToUnits;
    }

    public void setAssignTechsToUnits(final boolean assignTechsToUnits) {
        this.assignTechsToUnits = assignTechsToUnits;
    }
    //endregion Units

    //region Unit
    public ForceNamingType getForceNamingType() {
        return forceNamingType;
    }

    public void setForceNamingType(final ForceNamingType forceNamingType) {
        this.forceNamingType = forceNamingType;
    }

    public boolean isGenerateForceIcons() {
        return generateForceIcons;
    }

    public void setGenerateForceIcons(final boolean generateForceIcons) {
        this.generateForceIcons = generateForceIcons;
    }
    //endregion Unit

    //region Spares
    public boolean isGenerateMothballedSpareUnits() {
        return generateMothballedSpareUnits;
    }

    public void setGenerateMothballedSpareUnits(final boolean generateMothballedSpareUnits) {
        this.generateMothballedSpareUnits = generateMothballedSpareUnits;
    }

    public int getSparesPercentOfActiveUnits() {
        return sparesPercentOfActiveUnits;
    }

    public void setSparesPercentOfActiveUnits(final int sparesPercentOfActiveUnits) {
        this.sparesPercentOfActiveUnits = sparesPercentOfActiveUnits;
    }

    public boolean isGenerateSpareParts() {
        return generateSpareParts;
    }

    public void setGenerateSpareParts(final boolean generateSpareParts) {
        this.generateSpareParts = generateSpareParts;
    }

    public int getStartingArmourWeight() {
        return startingArmourWeight;
    }

    public void setStartingArmourWeight(final int startingArmourWeight) {
        this.startingArmourWeight = startingArmourWeight;
    }

    public boolean isGenerateSpareAmmunition() {
        return generateSpareAmmunition;
    }

    public void setGenerateSpareAmmunition(final boolean generateSpareAmmunition) {
        this.generateSpareAmmunition = generateSpareAmmunition;
    }

    public int getNumberReloadsPerWeapon() {
        return numberReloadsPerWeapon;
    }

    public void setNumberReloadsPerWeapon(final int numberReloadsPerWeapon) {
        this.numberReloadsPerWeapon = numberReloadsPerWeapon;
    }

    public boolean isGenerateFractionalMachineGunAmmunition() {
        return generateFractionalMachineGunAmmunition;
    }

    public void setGenerateFractionalMachineGunAmmunition(final boolean generateFractionalMachineGunAmmunition) {
        this.generateFractionalMachineGunAmmunition = generateFractionalMachineGunAmmunition;
    }
    //endregion Spares

    //region Contracts
    public boolean isSelectStartingContract() {
        return selectStartingContract;
    }

    public void setSelectStartingContract(final boolean selectStartingContract) {
        this.selectStartingContract = selectStartingContract;
    }

    public boolean isStartCourseToContractPlanet() {
        return startCourseToContractPlanet;
    }

    public void setStartCourseToContractPlanet(final boolean startCourseToContractPlanet) {
        this.startCourseToContractPlanet = startCourseToContractPlanet;
    }
    //endregion Contracts

    //region Finances
    public int getStartingCash() {
        return startingCash;
    }

    public void setStartingCash(final int startingCash) {
        this.startingCash = startingCash;
    }

    public boolean isRandomizeStartingCash() {
        return randomizeStartingCash;
    }

    public void setRandomizeStartingCash(final boolean randomizeStartingCash) {
        this.randomizeStartingCash = randomizeStartingCash;
    }

    public int getRandomStartingCashDiceCount() {
        return randomStartingCashDiceCount;
    }

    public void setRandomStartingCashDiceCount(final int randomStartingCashDiceCount) {
        this.randomStartingCashDiceCount = randomStartingCashDiceCount;
    }

    public int getMinimumStartingFloat() {
        return minimumStartingFloat;
    }

    public void setMinimumStartingFloat(final int minimumStartingFloat) {
        this.minimumStartingFloat = minimumStartingFloat;
    }

    public boolean isPayForSetup() {
        return payForSetup;
    }

    public void setPayForSetup(final boolean payForSetup) {
        this.payForSetup = payForSetup;
    }

    public boolean isStartingLoan() {
        return startingLoan;
    }

    public void setStartingLoan(final boolean startingLoan) {
        this.startingLoan = startingLoan;
    }

    public boolean isPayForPersonnel() {
        return payForPersonnel;
    }

    public void setPayForPersonnel(final boolean payForPersonnel) {
        this.payForPersonnel = payForPersonnel;
    }

    public boolean isPayForUnits() {
        return payForUnits;
    }

    public void setPayForUnits(final boolean payForUnits) {
        this.payForUnits = payForUnits;
    }

    public boolean isPayForParts() {
        return payForParts;
    }

    public void setPayForParts(final boolean payForParts) {
        this.payForParts = payForParts;
    }

    public boolean isPayForArmour() {
        return payForArmour;
    }

    public void setPayForArmour(final boolean payForArmour) {
        this.payForArmour = payForArmour;
    }

    public boolean isPayForAmmunition() {
        return payForAmmunition;
    }

    public void setPayForAmmunition(final boolean payForAmmunition) {
        this.payForAmmunition = payForAmmunition;
    }
    //endregion Finances
    //endregion Getters/Setters

    //region File IO
    public void writeToFile(File file) {
        if (file == null) {
            return;
        }
        String path = file.getPath();
        if (!path.endsWith(".xml")) {
            path += ".xml";
            file = new File(path);
        }
        try (OutputStream fos = new FileOutputStream(file);
             OutputStream bos = new BufferedOutputStream(fos);
             OutputStreamWriter osw = new OutputStreamWriter(bos, StandardCharsets.UTF_8);
             PrintWriter pw = new PrintWriter(osw)) {
            // Then save it out to that file.
            writeToXML(pw);
        } catch (Exception e) {
            MekHQ.getLogger().error(e);
        }
    }

    public void writeToXML(final PrintWriter pw) {
        writeToXML(pw, 0);
    }

    public void writeToXML(final PrintWriter pw, int indent) {
        MekHqXmlUtil.writeSimpleXMLOpenIndentedLine(pw, indent++, "companyGenerationOptions");
        // Base Information
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "type", getType().name());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "faction", getFaction().getShortName());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "specifyStartingPlanet", isSpecifyStartingPlanet());
        MekHqXmlUtil.writeSimpleXMLAttributedTag(pw, indent, "startingPlanet", "systemId",
                getStartingPlanet().getParentSystem().getId(), getStartingPlanet().getId());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "generateMercenaryCompanyCommandLance", isGenerateMercenaryCompanyCommandLance());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "companyCount", getCompanyCount());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "individualLanceCount", getIndividualLanceCount());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "lancesPerCompany", getLancesPerCompany());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "lanceSize", getLanceSize());

        // Personnel
        MekHqXmlUtil.writeSimpleXMLOpenIndentedLine(pw, indent++, "supportPersonnel");
        for (final Map.Entry<Integer, Integer> entry : getSupportPersonnel().entrySet()) {
            MekHqXmlUtil.writeSimpleXMLOpenIndentedLine(pw, indent++, "supportRole");
            MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "role", entry.getKey());
            MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "number", entry.getValue());
            MekHqXmlUtil.writeSimpleXMLCloseIndentedLine(pw, --indent, "supportRole");
        }
        MekHqXmlUtil.writeSimpleXMLCloseIndentedLine(pw, --indent, "supportPersonnel");
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "poolAssistants", isPoolAssistants());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "generateCaptains", isGenerateCaptains());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "assignCompanyCommanderFlag", isAssignCompanyCommanderFlag());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "companyCommanderLanceOfficer", isCompanyCommanderLanceOfficer());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "applyOfficerStatBonusToWorstSkill", isApplyOfficerStatBonusToWorstSkill());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "assignBestOfficers", isAssignBestOfficers());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "automaticallyAssignRanks", isAutomaticallyAssignRanks());

        // Personnel Randomization
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "randomizeOrigin", isRandomizeOrigin());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "randomizeAroundCentralPlanet", isRandomizeAroundCentralPlanet());
        MekHqXmlUtil.writeSimpleXMLAttributedTag(pw, indent, "centralPlanet", "systemId",
                getCentralPlanet().getParentSystem().getId(), getCentralPlanet().getId());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "originSearchRadius", getOriginSearchRadius());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "extraRandomOrigin", isExtraRandomOrigin());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "originDistanceScale", getOriginDistanceScale());

        // Units
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "generateUnitsAsAttached", isGenerateUnitsAsAttached());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "assignBestRollToUnitCommander", isAssignBestRollToUnitCommander());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "groupByWeight", isGroupByWeight());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "keepOfficerRollsSeparate", isKeepOfficerRollsSeparate());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "starLeagueYear", getStarLeagueYear());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "assignTechsToUnits", isAssignTechsToUnits());

        // Unit
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "forceNamingType", getForceNamingType().name());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "generateForceIcons", isGenerateForceIcons());

        // Spares
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "generateMothballedSpareUnits", isGenerateMothballedSpareUnits());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "sparesPercentOfActiveUnits", getSparesPercentOfActiveUnits());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "generateSpareParts", isGenerateSpareParts());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "startingArmourWeight", getStartingArmourWeight());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "generateSpareAmmunition", isGenerateSpareAmmunition());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "numberReloadsPerWeapon", getNumberReloadsPerWeapon());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "generateFractionalMachineGunAmmunition", isGenerateFractionalMachineGunAmmunition());

        // Contracts
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "selectStartingContract", isSelectStartingContract());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "startCourseToContractPlanet", isStartCourseToContractPlanet());

        // Finances
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "startingCash", getStartingCash());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "randomizeStartingCash", isRandomizeStartingCash());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "randomStartingCashDiceCount", getRandomStartingCashDiceCount());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "minimumStartingFloat", getMinimumStartingFloat());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "payForSetup", isPayForSetup());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "startingLoan", isStartingLoan());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "payForPersonnel", isPayForPersonnel());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "payForUnits", isPayForUnits());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "payForParts", isPayForParts());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "payForArmour", isPayForArmour());
        MekHqXmlUtil.writeSimpleXMLTag(pw, indent, "payForAmmunition", isPayForAmmunition());
        MekHqXmlUtil.writeSimpleXMLCloseIndentedLine(pw, --indent, "companyGenerationOptions");
    }

    public static CompanyGenerationOptions parseFromXML(final File file, final Campaign campaign) {
        if (file == null) {
            MekHQ.getLogger().error("Received a null file, returning the default AtB options");
            return new CompanyGenerationOptions(CompanyGenerationType.AGAINST_THE_BOT, campaign);
        }
        final Element element;

        // Open up the file.
        try (InputStream is = new FileInputStream(file)) {
            element = MekHqXmlUtil.newSafeDocumentBuilder().parse(is).getDocumentElement();
        } catch (Exception e) {
            MekHQ.getLogger().error("Failed to open file, returning the default AtB options", e);
            return new CompanyGenerationOptions(CompanyGenerationType.AGAINST_THE_BOT, campaign);
        }
        element.normalize();
        final NodeList nl = element.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            final Node wn = nl.item(i);
            if ("companyGenerationOptions".equals(wn.getNodeName())) {
                return parseFromXML(wn);
            }
        }
        MekHQ.getLogger().error("Failed to parse file, returning the default AtB options");
        return new CompanyGenerationOptions(CompanyGenerationType.AGAINST_THE_BOT, campaign);
    }

    public static CompanyGenerationOptions parseFromXML(final Node wn) {
        final NodeList nl = wn.getChildNodes();
        final CompanyGenerationOptions options = new CompanyGenerationOptions();

        try {
            for (int x = 0; x < nl.getLength(); x++) {
                final Node wn2 = nl.item(x);
                switch (wn2.getNodeName()) {
                    case "type":
                        options.setType(CompanyGenerationType.valueOf(wn2.getTextContent().trim()));
                        break;
                    case "faction":
                        options.setFaction(Factions.getInstance().getFaction(wn2.getTextContent().trim()));
                        break;
                    case "specifyStartingPlanet":
                        options.setSpecifyStartingPlanet(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "startingPlanet":
                        String startingPlanetSystemId = wn2.getAttributes().getNamedItem("systemId").getTextContent().trim();
                        String startingPlanetPlanetId = wn2.getTextContent().trim();
                        options.setStartingPlanet(Systems.getInstance().getSystemById(startingPlanetSystemId).getPlanetById(startingPlanetPlanetId));
                        break;
                    case "generateMercenaryCompanyCommandLance":
                        options.setGenerateMercenaryCompanyCommandLance(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "companyCount":
                        options.setCompanyCount(Integer.parseInt(wn2.getTextContent().trim()));
                        break;
                    case "individualLanceCount":
                        options.setIndividualLanceCount(Integer.parseInt(wn2.getTextContent().trim()));
                        break;
                    case "lancesPerCompany":
                        options.setLancesPerCompany(Integer.parseInt(wn2.getTextContent().trim()));
                        break;
                    case "lanceSize":
                        options.setLanceSize(Integer.parseInt(wn2.getTextContent().trim()));
                        break;
                    case "supportPersonnel": {
                        options.setSupportPersonnel(new HashMap<>());
                        final NodeList nl2 = wn.getChildNodes();
                        for (int y = 0; y < nl2.getLength(); y++) {
                            final Node wn3 = nl2.item(y);
                            if ("supportRole".equals(wn3.getNodeName())) {
                                final NodeList nl3 = wn3.getChildNodes();
                                int role = -1;
                                int number = -1;
                                for (int z = 0; z < nl3.getLength(); z++) {
                                    final Node wn4 = nl3.item(z);
                                    switch (wn4.getNodeName()) {
                                        case "role":
                                            role = Integer.parseInt(wn4.getTextContent().trim());
                                            break;
                                        case "number":
                                            number = Integer.parseInt(wn4.getTextContent().trim());
                                            break;
                                    }
                                }
                                if ((role != -1) && (number != -1)) {
                                    options.getSupportPersonnel().put(role, number);
                                }
                            }
                        }
                        break;
                    }
                    case "poolAssistants":
                        options.setPoolAssistants(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "generateCaptains":
                        options.setGenerateCaptains(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "assignCompanyCommanderFlag":
                        options.setAssignCompanyCommanderFlag(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "companyCommanderLanceOfficer":
                        options.setCompanyCommanderLanceOfficer(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "applyOfficerStatBonusToWorstSkill":
                        options.setApplyOfficerStatBonusToWorstSkill(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "assignBestOfficers":
                        options.setAssignBestOfficers(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "automaticallyAssignRanks":
                        options.setAutomaticallyAssignRanks(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "randomizeOrigin":
                        options.setRandomizeOrigin(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "randomizeAroundCentralPlanet":
                        options.setRandomizeAroundCentralPlanet(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "centralPlanet":
                        String centralPlanetSystemId = wn2.getAttributes().getNamedItem("systemId").getTextContent().trim();
                        String centralPlanetPlanetId = wn2.getTextContent().trim();
                        options.setCentralPlanet(Systems.getInstance().getSystemById(centralPlanetSystemId).getPlanetById(centralPlanetPlanetId));
                        break;
                    case "originSearchRadius":
                        options.setOriginSearchRadius(Integer.parseInt(wn2.getTextContent().trim()));
                        break;
                    case "extraRandomOrigin":
                        options.setExtraRandomOrigin(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "originDistanceScale":
                        options.setOriginDistanceScale(Double.parseDouble(wn2.getTextContent().trim()));
                        break;
                    case "generateUnitsAsAttached":
                        options.setGenerateUnitsAsAttached(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "assignBestRollToUnitCommander":
                        options.setAssignBestRollToUnitCommander(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "groupByWeight":
                        options.setGroupByWeight(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "keepOfficerRollsSeparate":
                        options.setKeepOfficerRollsSeparate(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "starLeagueYear":
                        options.setStarLeagueYear(Integer.parseInt(wn2.getTextContent().trim()));
                        break;
                    case "assignTechsToUnits":
                        options.setAssignTechsToUnits(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "forceNamingType":
                        options.setForceNamingType(ForceNamingType.valueOf(wn2.getTextContent().trim()));
                        break;
                    case "generateForceIcons":
                        options.setGenerateForceIcons(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "generateMothballedSpareUnits":
                        options.setGenerateMothballedSpareUnits(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "sparesPercentOfActiveUnits":
                        options.setSparesPercentOfActiveUnits(Integer.parseInt(wn2.getTextContent().trim()));
                        break;
                    case "generateSpareParts":
                        options.setGenerateSpareParts(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "startingArmourWeight":
                        options.setStartingArmourWeight(Integer.parseInt(wn2.getTextContent().trim()));
                        break;
                    case "generateSpareAmmunition":
                        options.setGenerateSpareAmmunition(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "numberReloadsPerWeapon":
                        options.setNumberReloadsPerWeapon(Integer.parseInt(wn2.getTextContent().trim()));
                        break;
                    case "generateFractionalMachineGunAmmunition":
                        options.setGenerateFractionalMachineGunAmmunition(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "selectStartingContract":
                        options.setSelectStartingContract(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "startCourseToContractPlanet":
                        options.setStartCourseToContractPlanet(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "startingCash":
                        options.setStartingCash(Integer.parseInt(wn2.getTextContent().trim()));
                        break;
                    case "randomizeStartingCash":
                        options.setRandomizeStartingCash(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "randomStartingCashDiceCount":
                        options.setRandomStartingCashDiceCount(Integer.parseInt(wn2.getTextContent().trim()));
                        break;
                    case "minimumStartingFloat":
                        options.setMinimumStartingFloat(Integer.parseInt(wn2.getTextContent().trim()));
                        break;
                    case "payForSetup":
                        options.setPayForSetup(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "startingLoan":
                        options.setStartingLoan(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "payForPersonnel":
                        options.setPayForPersonnel(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "payForUnits":
                        options.setPayForUnits(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "payForParts":
                        options.setPayForParts(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "payForArmour":
                        options.setPayForArmour(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                    case "payForAmmunition":
                        options.setPayForAmmunition(Boolean.parseBoolean(wn2.getTextContent().trim()));
                        break;
                }
            }
        } catch (Exception e) {
            MekHQ.getLogger().error(e);
        }
        return options;
    }
    //endregion File IO
}