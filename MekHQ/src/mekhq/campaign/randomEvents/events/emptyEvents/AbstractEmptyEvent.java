/*
 * Copyright (c) 2020 - The MegaMek Team. All rights reserved.
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MekHQ.  If not, see <http://www.gnu.org/licenses/>.
 */
package mekhq.campaign.randomEvents.events.emptyEvents;

import mekhq.campaign.randomEvents.AbstractRandomEvent;
import mekhq.campaign.randomEvents.enums.RandomEventType;

public abstract class AbstractEmptyEvent extends AbstractRandomEvent {
    //region Constructors
    protected AbstractEmptyEvent(String name) {
        super(name);
    }
    //endregion Constructors

    //region Initialisation Methods
    /**
     * This is initializing the RandomEventType to RandomEventType.EMPTY
     * @see AbstractRandomEvent#initializeRandomEventType() for more information
     */
    protected void initializeRandomEventType() {
        this.type = RandomEventType.EMPTY;
    }

    /**
     * This is being overridden for readability
     * @see AbstractRandomEvent#initializeRandomEventWeight() for more information
     */
    @Override
    protected abstract void initializeRandomEventWeight();

    /**
     * This is being overridden for readability
     * @see AbstractRandomEvent#initializeDescriptionAndOptions() for more information
     */
    @Override
    protected abstract void initializeDescriptionAndOptions();
    //endregion Initialisation Methods

    //region Generation
    /**
     * This is being overridden for readability
     * @see AbstractRandomEvent#isViable() for more information
     */
    @Override
    public abstract boolean isViable();
    //endregion Generation

    //region Processing
    /**
     * This is being overridden for readability
     * @see AbstractRandomEvent#process(int) for more information
     */
    @Override
    public abstract void process(int optionIndex);
    //endregion Processing
}