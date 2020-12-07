/*
 * Copyright (c) 2020 - The MegaMek Team. All Rights Reserved.
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
package mekhq.campaign.personnel.randomDeath;

import megamek.common.enums.Gender;
import mekhq.campaign.Campaign;
import mekhq.campaign.personnel.Injury;
import mekhq.campaign.personnel.Person;
import mekhq.campaign.personnel.enums.AgeRange;
import mekhq.campaign.personnel.enums.InjuryLevel;
import mekhq.campaign.personnel.enums.PersonnelStatus;
import mekhq.campaign.personnel.enums.RandomDeathType;

public abstract class AbstractRandomDeathMethod {
    //region Variable Declarations
    protected RandomDeathType type;
    //endregion Variable Declarations

    //region Constructors
    protected AbstractRandomDeathMethod(RandomDeathType type) {
        this.type = type;
    }
    //endregion Constructors

    /**
     * @param campaign the campaign the person is in
     * @param ageRange the person's age range
     * @param age the person's age
     * @param gender the person's gender
     * @return true if the person is selected to randomly die, otherwise false
     */
    public abstract boolean randomDeath(Campaign campaign, AgeRange ageRange, int age, Gender gender);

    /**
     * @param campaign the campaign the person is in
     * @param ageRange the person's age range
     * @return true if the random death is enabled for the age
     */
    public boolean validateAgeEnabled(Campaign campaign, AgeRange ageRange) {
        switch (ageRange) {
            case ELDER:
            case ADULT:
                return true;
            case TEENAGER:
                return campaign.getCampaignOptions().teenDeathsEnabled();
            case PRETEEN:
                return campaign.getCampaignOptions().preteenDeathsEnabled();
            case CHILD:
                return campaign.getCampaignOptions().childDeathsEnabled();
            case TODDLER:
                return campaign.getCampaignOptions().toddlerDeathsEnabled();
            case BABY:
                return campaign.getCampaignOptions().infantMortalityEnabled();
            default:
                return false;
        }
    }

    //region Cause
    /**
     * @param person the person who has died
     * @param ageRange the person's age range
     * @param campaign the campaign the person is a part of
     * @return the cause of the Person's random death
     */
    public PersonnelStatus getCause(Person person, AgeRange ageRange, Campaign campaign) {
        if (person.getStatus().isMIA()) {
            return PersonnelStatus.KIA;
        } else if (person.hasInjuries(false)) {
            final PersonnelStatus status = determineIfInjuriesCausedTheDeath(person);
            if (!status.isActive()) {
                return status;
            }
        }

        if (person.isPregnant() && person.getPregnancyWeek(campaign.getLocalDate()) > 22) {
            return PersonnelStatus.PREGNANCY_COMPLICATIONS;
        } else if (ageRange.isElder()) {
            return PersonnelStatus.OLD_AGE;
        }

        return PersonnelStatus.NATURAL_CAUSES;
    }

    /**
     * @param person the person from whom may have died of injuries
     * @return the personnel status applicable to the form of injury that caused the death, or
     * ACTIVE if it wasn't determined that injuries caused the death
     */
    private PersonnelStatus determineIfInjuriesCausedTheDeath(Person person) {
        for (Injury injury : person.getInjuries()) {
            InjuryLevel level = injury.getLevel();

            // We care about injuries that are major or deadly. We do not want any chronic
            // conditions nor scratches
            if ((level == InjuryLevel.DEADLY) || (level == InjuryLevel.MAJOR)) {
                return PersonnelStatus.WOUNDS;
            }
        }

        return PersonnelStatus.ACTIVE;
    }
    //endregion Cause
}