package ca.aequilibrium.base.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ca.aequilibrium.base.vo.transformer.Transformer

@Database(entities = arrayOf(Transformer::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transformerDao(): TransformerDao

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(context).also { instance = it }
            }
        }

        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "transformers-db")
                .build()
        }
    }
}