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
package mekhq.gui.dialog.icons;

import megamek.client.ui.swing.dialog.imageChooser.AbstractIconChooser;
import megamek.common.icons.AbstractIcon;
import megamek.common.util.fileUtils.DirectoryItems;
import mekhq.MHQStaticDirectoryManager;
import mekhq.icons.StandardForceIcon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StandardForceIconChooser extends AbstractIconChooser {
    //region Constructors
    public StandardForceIconChooser() {
        this(null);
    }

    public StandardForceIconChooser(AbstractIcon icon) {
        super(new StandardForceIconChooserTree(), icon);
    }
    //endregion Constructors

    @Override
    protected DirectoryItems getDirectory() {
        return MHQStaticDirectoryManager.getForceIcons();
    }

    @Override
    protected AbstractIcon createIcon(String category, String filename) {
        return new StandardForceIcon(category, filename);
    }

    @Override
    protected List<AbstractIcon> getItems(String category) {
        List<AbstractIcon> result = new ArrayList<>();

        // The portraits of the selected category are presented.
        // When the includeSubDirs flag is true, all categories
        // below the selected one are also presented.
        if (includeSubDirs) {
            for (Iterator<String> catNames = getDirectory().getCategoryNames(); catNames.hasNext(); ) {
                String tcat = catNames.next();
                if (tcat.startsWith(category)) {
                    addCategoryItems(tcat, result);
                }
            }
        } else {
            addCategoryItems(category, result);
        }
        return result;
    }

    /** Reloads the portrait directory from disk. */
    @Override
    protected void refreshDirectory() {
        MHQStaticDirectoryManager.refreshForceIcons();
        refreshDirectory(new StandardForceIconChooserTree());
    }
}
