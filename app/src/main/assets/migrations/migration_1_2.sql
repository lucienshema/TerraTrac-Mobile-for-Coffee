CREATE TABLE IF NOT EXISTS `Akrabis` (
                `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                `akrabiNumber` TEXT NOT NULL,
                `akrabiName` TEXT NOT NULL,
                `siteName` TEXT NOT NULL,
                `age` INTEGER NOT NULL DEFAULT 0,
                `gender` TEXT NOT NULL DEFAULT '',
                `woreda` TEXT NOT NULL DEFAULT '',
                `kebele` TEXT NOT NULL DEFAULT '',
                `govtIdNumber` TEXT NOT NULL DEFAULT '',
                `phone` TEXT NOT NULL DEFAULT '',
                `photoUri` TEXT
            );

