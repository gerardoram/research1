/*******************************************************************************
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 ******************************************************************************/

package com.hybris.mobile.lib.b2b.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

import com.hybris.mobile.lib.b2b.provider.CatalogContract;
import com.hybris.mobile.lib.b2b.provider.CatalogProvider;

import junit.framework.Assert;


/**
 * Catalog provider tests
 */
public class CatalogProviderTests extends ProviderTestCase2<CatalogProvider> {

    private static final int NB_DATA = 20;
    private static final int NB_GROUP = 3;
    private static String authority;

    public CatalogProviderTests() {
        // The authority name must match the one on R.string.provider_authority (we cannot get it through getString before invoking the constructor)
        super(CatalogProvider.class, "com.hybris.mobile.lib.b2b.provider");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        authority = getContext().getString(R.string.provider_authority);

        for (int i = 0; i < NB_DATA; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CatalogContract.DataBaseData.ATT_DATA_ID, i);
            contentValues.put(CatalogContract.DataBaseData.ATT_DATA, "Test data number " + i);
            getMockContentResolver().insert(Uri.withAppendedPath(CatalogContract.Provider.getUriData(authority), i + ""), contentValues);
            getMockContentResolver().insert(Uri.withAppendedPath(CatalogContract.Provider.getUriDataDetails(authority), i + ""), contentValues);

            for (int j = 0; j < NB_GROUP; j++) {
                contentValues = new ContentValues();
                contentValues.put(CatalogContract.DataBaseDataLinkGroup.ATT_DATA_ID, i);
                contentValues.put(CatalogContract.DataBaseDataLinkGroup.ATT_GROUP_ID, j);
                getMockContentResolver().insert(Uri.withAppendedPath(CatalogContract.Provider.getUriGroup(authority), i + ""), contentValues);
            }

        }

    }

    public void testGetDataByGroup() {
        for (int i = 0; i < NB_GROUP; i++) {
            Cursor cursor = getMockContentResolver().query(Uri.withAppendedPath(CatalogContract.Provider.getUriGroup(authority), i + ""), null,
                    null, null, null);
            Assert.assertTrue(cursor.getCount() > 0);
        }
    }

    public void testGetDataById() {
        for (int i = 0; i < NB_DATA; i++) {
            Cursor cursor = getMockContentResolver().query(Uri.withAppendedPath(CatalogContract.Provider.getUriData(authority), i + ""), null,
                    null, null, null);
            Assert.assertTrue(cursor.getCount() > 0);
            cursor = getMockContentResolver().query(Uri.withAppendedPath(CatalogContract.Provider.getUriDataDetails(authority), i + ""), null,
                    null, null, null);
            Assert.assertTrue(cursor.getCount() > 0);
        }
    }

    public void testUpdateData() {

        for (int j = 0; j < NB_DATA; j++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CatalogContract.DataBaseData.ATT_DATA_ID, j + "test");

            Assert.assertTrue(getMockContentResolver().update(Uri.withAppendedPath(CatalogContract.Provider.getUriData(authority), j + ""),
                    contentValues, null, null) == 1);
            Assert.assertTrue(getMockContentResolver().update(Uri.withAppendedPath(CatalogContract.Provider.getUriDataDetails(authority), j + ""),
                    contentValues, null, null) == 1);

            for (int i = 0; i < NB_GROUP; i++) {
                contentValues = new ContentValues();
                contentValues.put(CatalogContract.DataBaseDataLinkGroup.ATT_DATA_ID, j + "test");
                contentValues.put(CatalogContract.DataBaseDataLinkGroup.ATT_GROUP_ID, i);
                getMockContentResolver().insert(Uri.withAppendedPath(CatalogContract.Provider.getUriGroup(authority), j + ""), contentValues);
            }
        }

        for (int i = 0; i < NB_DATA; i++) {
            Cursor cursor = getMockContentResolver().query(Uri.withAppendedPath(CatalogContract.Provider.getUriData(authority), i + ""), null,
                    null, null, null);
            Assert.assertTrue(cursor.getCount() == 0);
            cursor = getMockContentResolver().query(Uri.withAppendedPath(CatalogContract.Provider.getUriDataDetails(authority), i + ""), null,
                    null, null, null);
            Assert.assertTrue(cursor.getCount() == 0);
        }

        for (int i = 0; i < NB_DATA; i++) {
            Cursor cursor = getMockContentResolver().query(Uri.withAppendedPath(CatalogContract.Provider.getUriData(authority), i + "test"),
                    null, null, null, null);
            Assert.assertTrue(cursor.getCount() > 0);
            cursor = getMockContentResolver().query(Uri.withAppendedPath(CatalogContract.Provider.getUriDataDetails(authority), i + "test"),
                    null, null, null, null);
            Assert.assertTrue(cursor.getCount() > 0);
        }

    }

    public void testDeleteAllDataGroupLinks() {
        getMockContentResolver().delete(CatalogContract.Provider.getUriGroup(authority), null, null);

        for (int j = 0; j < NB_GROUP; j++) {
            Cursor cursor = getMockContentResolver().query(Uri.withAppendedPath(CatalogContract.Provider.getUriGroup(authority), j + ""), null,
                    null, null, null);
            Assert.assertTrue(cursor.getCount() == 0);
        }

    }

    public void testDeleteDataGroupLinksById() {
        getMockContentResolver().delete(Uri.withAppendedPath(CatalogContract.Provider.getUriGroup(authority), "1"), null, null);

        Cursor cursor = getMockContentResolver().query(Uri.withAppendedPath(CatalogContract.Provider.getUriGroup(authority), "1"), null, null,
                null, null);
        Assert.assertTrue(cursor.getCount() == 0);
    }

    public void testDeleteAllData() {
        getMockContentResolver().delete(CatalogContract.Provider.getUriData(authority), null, null);
        getMockContentResolver().delete(CatalogContract.Provider.getUriDataDetails(authority), null, null);

        for (int i = 0; i < NB_DATA; i++) {
            Cursor cursor = getMockContentResolver().query(Uri.withAppendedPath(CatalogContract.Provider.getUriData(authority), i + ""), null,
                    null, null, null);
            Assert.assertTrue(cursor.getCount() == 0);
            cursor = getMockContentResolver().query(Uri.withAppendedPath(CatalogContract.Provider.getUriDataDetails(authority), i + ""), null,
                    null, null, null);
            Assert.assertTrue(cursor.getCount() == 0);
        }
    }

    public void testDeleteDataById() {
        for (int i = 0; i < NB_DATA; i++) {
            getMockContentResolver().delete(Uri.withAppendedPath(CatalogContract.Provider.getUriData(authority), i + ""), null, null);
            getMockContentResolver().delete(Uri.withAppendedPath(CatalogContract.Provider.getUriDataDetails(authority), i + ""), null, null);
        }

        for (int i = 0; i < NB_DATA; i++) {
            Cursor cursor = getMockContentResolver().query(Uri.withAppendedPath(CatalogContract.Provider.getUriData(authority), i + ""), null,
                    null, null, null);
            Assert.assertTrue(cursor.getCount() == 0);
            cursor = getMockContentResolver().query(Uri.withAppendedPath(CatalogContract.Provider.getUriDataDetails(authority), i + ""), null,
                    null, null, null);
            Assert.assertTrue(cursor.getCount() == 0);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        for (int i = 0; i < NB_DATA; i++) {
            getMockContentResolver().delete(Uri.withAppendedPath(CatalogContract.Provider.getUriData(authority), i + ""), null, null);
            getMockContentResolver().delete(Uri.withAppendedPath(CatalogContract.Provider.getUriDataDetails(authority), i + ""), null, null);
        }

        getMockContentResolver().delete(CatalogContract.Provider.getUriGroup(authority), null, null);
    }

}
