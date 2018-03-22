package com.palash.sampleapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.palash.sampleapp.entiry.ELLogin;
import com.palash.sampleapp.entiry.ELSync;

import java.util.ArrayList;

public class DatabaseAdapter {

    DatabaseContract databaseContract = null;

    public DatabaseAdapter(DatabaseContract contract) {
        databaseContract = contract;
    }

    public void close() {
        if (databaseContract != null) {
            databaseContract.close();
        }
    }

    public class SyncAdapter {

        private String[] projection = {
                DatabaseContract.Sync.DATE_TIME,
                DatabaseContract.Sync.CURRENT_SYNC,
                DatabaseContract.Sync.IS_SYNCING
        };

        public long Add(ELSync ELSync) {
            long res = 0;
            try {
                ContentValues values = SyncToContentValues(ELSync);
                if (values != null) {
                    if (CountByID("1") == 0) {
                        res = databaseContract.open().insert(DatabaseContract.Sync.TABLE_NAME, null, values);
                    } else {
                        Update(ELSync);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                close();
            }
            return res;
        }

        public long Update(ELSync ELSync) {
            long res = 0;
            try {
                ContentValues values = SyncToContentValues(ELSync);
                if (values != null) {
                    String WhereClause = DatabaseContract.Sync._ID + "=1";
                    res = databaseContract.open().update(DatabaseContract.Sync.TABLE_NAME, values, WhereClause, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                close();
            }
            return res;
        }

        private ArrayList<ELSync> CursorToArrayList(Cursor result) {
            ArrayList<ELSync> objELSyncs = null;
            if (result != null) {
                objELSyncs = new ArrayList<ELSync>();
                while (result.moveToNext()) {
                    ELSync objELSync = new ELSync();
                    objELSync.setDateTime(result.getString(result.getColumnIndex(DatabaseContract.Sync.DATE_TIME)));
                    objELSync.setCurrentSync(result.getString(result.getColumnIndex(DatabaseContract.Sync.CURRENT_SYNC)));
                    objELSync.setIsSyncing(result.getString(result.getColumnIndex(DatabaseContract.Sync.IS_SYNCING)));
                    objELSyncs.add(objELSync);
                }
            }
            return objELSyncs;
        }

        public ContentValues SyncToContentValues(ELSync ELSync) {
            ContentValues values = null;
            if (ELSync != null) {
                values = new ContentValues();
                values.put(DatabaseContract.Sync.DATE_TIME, ELSync.getDateTime());
                values.put(DatabaseContract.Sync.CURRENT_SYNC, ELSync.getCurrentSync());
                values.put(DatabaseContract.Sync.IS_SYNCING, ELSync.getIsSyncing());
            }
            return values;
        }

        public ArrayList<ELSync> listAll() {
            ArrayList<ELSync> objELSyncs = null;
            Cursor result = null;
            try {
                result = databaseContract.open().query(DatabaseContract.Sync.TABLE_NAME, projection, null, null, null, null, null);
                objELSyncs = CursorToArrayList(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return objELSyncs;
        }

        public ELSync GetSync() {
            ArrayList<ELSync> ELSyncArrayList = null;
            ELSync ELSync = null;
            Cursor result = null;
            try {
                ELSyncArrayList = listAll();
                if (ELSyncArrayList != null && ELSyncArrayList.size() > 0) {
                    ELSync = ELSyncArrayList.get(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ELSync;
        }

        public ArrayList<ELSync> listAll(String SyncID) {
            ArrayList<ELSync> objELSyncs = new ArrayList<ELSync>();
            Cursor result = null;
            try {
                String WhereClause = DatabaseContract.Sync._ID + "='" + SyncID + "'";
                result = databaseContract.open().query(DatabaseContract.Sync.TABLE_NAME,
                        projection, WhereClause,
                        null, null,
                        null, null);
                objELSyncs = CursorToArrayList(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return objELSyncs;
        }

        public ArrayList<ELSync> Find(String SyncID) {
            ArrayList<ELSync> objELSyncs = new ArrayList<ELSync>();
            Cursor result = null;
            try {
                String WhereClause = DatabaseContract.Sync._ID + "='" + SyncID + "'";
                result = databaseContract.open().query(DatabaseContract.Sync.TABLE_NAME,
                        projection, WhereClause,
                        null, null,
                        null, null);
                objELSyncs = CursorToArrayList(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return objELSyncs;
        }

        public int Count() {
            int res = 0;
            ArrayList<ELSync> list = listAll();
            if (list != null) {
                res = list.size();
            }
            return res;
        }

        public int CountByID(String ID) {
            int res = 0;
            ArrayList<ELSync> list = Find(ID);
            if (list != null) {
                res = list.size();
            }
            return res;
        }

        public void Delete() {
            try {
                databaseContract.open().delete(DatabaseContract.Sync.TABLE_NAME, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class LoginAdapter {

        String[] projection = {
                DatabaseContract.Login.COLUMN_NAME_USER_ID,
                DatabaseContract.Login.COLUMN_NAME_LOGIN_NAME,
                DatabaseContract.Login.COLUMN_NAME_PASSWORD,
                DatabaseContract.Login.COLUMN_NAME_LOGIN_STATUS,
                DatabaseContract.Login.COLUMN_NAME_REMEMBER_ME
        };

        private ContentValues LoginToContentValues(ELLogin elLogin) {
            ContentValues values = null;
            try {
                if (elLogin != null) {
                    values = new ContentValues();
                    values.put(DatabaseContract.Login.COLUMN_NAME_USER_ID, elLogin.getUserId());
                    values.put(DatabaseContract.Login.COLUMN_NAME_LOGIN_NAME, elLogin.getLoginName());
                    values.put(DatabaseContract.Login.COLUMN_NAME_PASSWORD, elLogin.getPassword());
                    values.put(DatabaseContract.Login.COLUMN_NAME_LOGIN_STATUS, elLogin.getLoginStatus());
                    values.put(DatabaseContract.Login.COLUMN_NAME_REMEMBER_ME, elLogin.getRememberMe());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return values;
        }

        private ArrayList<ELLogin> CursorToArrayList(Cursor result) {
            ArrayList<ELLogin> listLogin = null;
            try {
                if (result != null) {
                    listLogin = new ArrayList<ELLogin>();
                    while (result.moveToNext()) {
                        ELLogin elLogin = new ELLogin();
                        elLogin.setUserId(result.getString(result.getColumnIndex(DatabaseContract.Login.COLUMN_NAME_USER_ID)));
                        elLogin.setPassword(result.getString(result.getColumnIndex(DatabaseContract.Login.COLUMN_NAME_PASSWORD)));
                        elLogin.setLoginName(result.getString(result.getColumnIndex(DatabaseContract.Login.COLUMN_NAME_LOGIN_NAME)));
                        elLogin.setLoginStatus(result.getString(result.getColumnIndex(DatabaseContract.Login.COLUMN_NAME_LOGIN_STATUS)));
                        elLogin.setRememberMe(result.getString(result.getColumnIndex(DatabaseContract.Login.COLUMN_NAME_REMEMBER_ME)));
                        listLogin.add(elLogin);
                    }
                    result.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return listLogin;
        }

        public long create(ELLogin elLogin) {
            long rowId = -1;
            try {
                CheckLogin();
                ContentValues values = LoginToContentValues(elLogin);
                if (values != null) {
                    SQLiteDatabase db = databaseContract.open();
                    rowId = db.insert(DatabaseContract.Login.TABLE_NAME, null, values);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                databaseContract.close();
            }
            return rowId;
        }

        private void CheckLogin() {
            try {
                SQLiteDatabase db = databaseContract.open();
                db.delete(DatabaseContract.Login.TABLE_NAME, null, null);
                databaseContract.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public long updatePassword(ELLogin elLogin) {
            long rowId = -1;
            try {
                ContentValues values = new ContentValues();
                values.put(DatabaseContract.Login.COLUMN_NAME_PASSWORD, elLogin.getPassword());
                String whereClause = null;
                whereClause = DatabaseContract.Login.COLUMN_NAME_USER_ID + " = " + elLogin.getUserId();
                rowId = databaseContract.open().update(
                        DatabaseContract.Login.TABLE_NAME, values, whereClause, null);
            } catch (SQLException e) {
                e.printStackTrace();
                ;
            } finally {
                databaseContract.close();
            }
            return rowId;
        }

        public long UpdateStatus(ELLogin profile) {
            long rowId = -1;
            try {
                ContentValues values = new ContentValues();
                values.put(DatabaseContract.Login.COLUMN_NAME_USER_ID, profile.getUserId());
                values.put(DatabaseContract.Login.COLUMN_NAME_LOGIN_NAME, profile.getLoginName());
                values.put(DatabaseContract.Login.COLUMN_NAME_PASSWORD, profile.getPassword());
                values.put(DatabaseContract.Login.COLUMN_NAME_LOGIN_STATUS, profile.getLoginStatus());
                values.put(DatabaseContract.Login.COLUMN_NAME_REMEMBER_ME, profile.getRememberMe());
                String whereClause = null;
                whereClause = DatabaseContract.Login._ID + " = 1 ";
                rowId = databaseContract.open().update(
                        DatabaseContract.Login.TABLE_NAME, values, whereClause, null);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                databaseContract.close();
            }
            return rowId;
        }

        public ArrayList<ELLogin> listAll() {
            ArrayList<ELLogin> listLogin = null;
            Cursor result = null;
            try {
                SQLiteDatabase db = databaseContract.open();
                result = db.query(DatabaseContract.Login.TABLE_NAME,
                        projection, null,
                        null, null, null, null);
                listLogin = CursorToArrayList(result);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return listLogin;
        }

        public void delete() {
            try {
                SQLiteDatabase db = databaseContract.open();
                db.delete(DatabaseContract.Login.TABLE_NAME, null, null);
                db.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}