/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.content.actions;

import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.test.InstrumentationTestCase;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.test.R;
import org.junit.Test;

import java.io.File;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;

public class SetLookActionTest extends InstrumentationTestCase {

	protected static final int IMAGE_FILE_ID = R.raw.icon;
	protected String projectName = "testProject";
	protected File testImage;
	protected Project project;
	protected Sprite sprite;
	protected LookData firstLookData;
	protected LookData secondLookData;

	@Override
	protected void setUp() throws Exception {
		File projectDir = new File(Constants.DEFAULT_ROOT_DIRECTORY, projectName);

		if (projectDir.exists()) {
			StorageOperations.deleteDir(projectDir);
		}

		project = new Project(getInstrumentation().getTargetContext(), projectName);
		XstreamSerializer.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		testImage = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				R.raw.icon,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				"testImage.png",
				1);

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(this.testImage.getAbsolutePath(), bitmapOptions);

		ScreenValues.SCREEN_HEIGHT = 200;
		ScreenValues.SCREEN_WIDTH = 200;

		sprite = new SingleSprite("new sprite");
		project.getDefaultScene().addSprite(sprite);
		firstLookData = new LookData();
		firstLookData.setFileName(testImage.getName());
		firstLookData.setName("first look");
		secondLookData = new LookData();
		secondLookData.setFileName(testImage.getName());
		secondLookData.setName("second look");
		sprite.getLookList().add(firstLookData);
		sprite.getLookList().add(secondLookData);
	}

	@Override
	protected void tearDown() throws Exception {
		File projectDir = new File(Constants.DEFAULT_ROOT_DIRECTORY, projectName);

		if (projectDir.exists()) {
			StorageOperations.deleteDir(projectDir);
		}
		super.tearDown();
	}

	@Test
	public void testSetLook() {
		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createSetLookAction(sprite, firstLookData);
		action.act(1.0f);
		assertEquals(firstLookData, sprite.look.getLookData());
	}

	@Test
	public void testSetLookByIndex() {
		Formula formula = new Formula(1);

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createSetLookByIndexAction(sprite, formula);
		action.act(1.0f);
		assertEquals(firstLookData, sprite.look.getLookData());

		formula = new Formula(2);
		action = factory.createSetLookByIndexAction(sprite, formula);
		action.act(1.0f);
		assertEquals(secondLookData, sprite.look.getLookData());
	}

	@Test
	public void testSetLookByWrongIndex() {
		sprite.look.setLookData(firstLookData);

		Formula formula = new Formula(-1);
		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createSetLookByIndexAction(sprite, formula);
		action.act(1.0f);
		assertEquals(firstLookData, sprite.look.getLookData());

		formula = new Formula(42);
		action = factory.createSetLookByIndexAction(sprite, formula);
		action.act(1.0f);
		assertEquals(firstLookData, sprite.look.getLookData());
	}
}
