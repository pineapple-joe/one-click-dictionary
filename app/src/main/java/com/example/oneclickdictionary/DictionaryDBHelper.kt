package com.example.oneclickdictionary

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.IOException


public class DictionaryDBHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "dictionary.db"
        private const val DATABASE_VERSION = 1
        private const val DATABASE_DATA_FILE = "english_dictionary.csv"
        private const val TABLE_DICTIONARY = "dictionary"
        private const val TABLE_MY_WORDS = "words"
        private const val KEY_WORD = "word"
        private const val KEY_DEFINITION = "definition"
    }

    fun createDatabase() {
        val db = writableDatabase
        if (!tableExists(db, TABLE_DICTIONARY)){
            this.initDatabase(db)
        }
    }

    private fun tableExists(sqLiteDatabase: SQLiteDatabase?, table: String?): Boolean {
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen || table == null) {
            return false
        }
        var count = 0
        val args = arrayOf("table", table)
        val cursor = sqLiteDatabase.rawQuery(
            "SELECT COUNT(*) FROM sqlite_master WHERE type=? AND name=?",
            args,
            null
        )
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count > 0
    }

    private fun initDatabase(db: SQLiteDatabase) {
        val csvHelper = CSVHelper()
        val inputStream = csvHelper.readCSV(DATABASE_DATA_FILE, context)
        var line: String?
        db.beginTransaction()
        try {
            while (inputStream.readLine().also { line = it } != null) {
                val columns = csvHelper.splitLine(line)
                if (columns.size != 3) {
                    Log.d("CSVParser", "Skipping Bad CSV Row")
                    continue
                }
                val cv = ContentValues(3)
                cv.put(KEY_WORD, columns[0].trim { it <= ' ' })
                cv.put(KEY_DEFINITION, columns[2].trim { it <= ' ' })
                db.insert(TABLE_DICTIONARY, null, cv)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream.close()
            } catch (e: Exception) {
                println("An error occurred while closing the file: ${e.message}")
            }
        }
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createDictionaryTable = (((("CREATE TABLE $TABLE_DICTIONARY").toString() + "("
                + KEY_WORD) + " TEXT,"
                + KEY_DEFINITION) + " TEXT" + ")")
        db!!.execSQL(createDictionaryTable)

        val createMyWordsTable = (((("CREATE TABLE $TABLE_MY_WORDS").toString() + "("
                + KEY_WORD) + " TEXT,"
                + KEY_DEFINITION) + " TEXT" + ")")
        db.execSQL(createMyWordsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + TABLE_DICTIONARY);
        onCreate(db);
    }

    fun getWord(wordToFind: String): MutableList<Word> {
        val wordDefinitions = mutableListOf<Word>()
        try {
            val db = this.readableDatabase

            val cursor = db.query(
                TABLE_DICTIONARY,
                arrayOf(KEY_DEFINITION),
                "$KEY_WORD=?",
                arrayOf(wordToFind.replaceFirstChar { it.uppercase() }),
                null, null, null, null
            )


            with(cursor) {
                while (moveToNext()) {
                    val definition = getString(cursor.getColumnIndexOrThrow(KEY_DEFINITION))
                    val word = Word(wordToFind, definition)
                    wordDefinitions.add(word)
                }
            }
            cursor.close()
            return wordDefinitions
        }
        catch (e: Exception){
            println("An error occurred while getting word definition: ${e.message}")
        }
        return wordDefinitions

    }

    fun addWord(word: String, definition:String) {
        val db = this.writableDatabase
        try {
            val values = ContentValues()
            values.put(KEY_WORD, word)
            values.put(KEY_DEFINITION, definition)
            db.insert(TABLE_MY_WORDS, null, values)
        } catch (e: Exception){
            println("An error occurred while adding word definition: ${e.message}")
        } finally {
            db.close()
        }
    }
}