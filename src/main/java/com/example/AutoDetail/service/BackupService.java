package com.example.AutoDetail.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class BackupService {

    private static final String BACKUP_DIR = "backups";
    private static final String DB_BACKUP_DIR = "database-backups";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    /**
     * Еженедельное резервное копирование (каждое воскресенье в 2:00 ночи)
     */
    @Scheduled(cron = "0 0 2 ? * SUN") // Каждое воскресенье в 2:00
    public void performWeeklyBackup() {
        try {
            System.out.println("🚀 Запуск еженедельного резервного копирования: " + LocalDateTime.now());

            // Создаем директории для бэкапов
            createBackupDirectories();

            // Выполняем резервное копирование данных
            boolean dataBackupSuccess = backupApplicationData();
            boolean dbBackupSuccess = backupDatabase();

            if (dataBackupSuccess && dbBackupSuccess) {
                System.out.println("✅ Еженедельное резервное копирование успешно завершено");
                cleanupOldBackups(); // Очистка старых бэкапов
            } else {
                System.out.println("⚠️ Резервное копирование завершено с ошибками");
            }

        } catch (Exception e) {
            System.err.println("❌ Ошибка при резервном копировании: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Резервное копирование данных приложения (экспорт в Excel)
     */
    private boolean backupApplicationData() {
        try {
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            String backupFileName = BACKUP_DIR + "/app_data_backup_" + timestamp + ".zip";

            try (FileOutputStream fos = new FileOutputStream(backupFileName);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                // Экспортируем данные в Excel и добавляем в архив
                exportDataToBackup(zos);
            }

            System.out.println("✅ Резервная копия данных приложения создана: " + backupFileName);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Ошибка создания резервной копии данных: " + e.getMessage());
            return false;
        }
    }

    /**
     * Резервное копирование базы данных
     */
    private boolean backupDatabase() {
        try {
            String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
            String backupFileName = DB_BACKUP_DIR + "/db_backup_" + timestamp + ".sql";

            // Здесь должна быть логика экспорта БД
            // Для примера создаем файл с информацией о бэкапе
            String backupInfo = createDatabaseBackupInfo();
            Files.write(Paths.get(backupFileName), backupInfo.getBytes());

            System.out.println("✅ Резервная копия БД создана: " + backupFileName);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Ошибка создания резервной копии БД: " + e.getMessage());
            return false;
        }
    }

    /**
     * Экспорт данных в Excel для бэкапа
     */
    private void exportDataToBackup(ZipOutputStream zos) throws IOException {
        ExportImportService exportService = getExportService();

        // Добавляем товары в архив
        addToZip(zos, exportService.exportItemsToExcel(), "items_backup.xlsx");

        // Добавляем поставщиков в архив
        addToZip(zos, exportService.exportSuppliersToExcel(), "suppliers_backup.xlsx");

        // Добавляем категории в архив
        addToZip(zos, exportService.exportCategoriesToExcel(), "categories_backup.xlsx");

        // Добавляем файл с информацией о бэкапе
        String backupInfo = createBackupInfo();
        addToZip(zos, backupInfo.getBytes(), "backup_info.txt");
    }

    /**
     * Добавление файла в ZIP архив
     */
    private void addToZip(ZipOutputStream zos, byte[] data, String fileName) throws IOException {
        ZipEntry entry = new ZipEntry(fileName);
        zos.putNextEntry(entry);
        zos.write(data);
        zos.closeEntry();
    }

    /**
     * Создание информации о бэкапе
     */
    private String createBackupInfo() {
        return String.format("""
            AutoDetail Backup Information
            ============================
            Backup Date: %s
            Application: AutoDetail Management System
            Backup Type: Weekly Automatic Backup
            Contents:
            - Items data (Excel)
            - Suppliers data (Excel)
            - Categories data (Excel)
            - Database schema information
            
            Generated by: AutoDetail Backup Service
            """, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    /**
     * Создание информации о бэкапе БД
     */
    private String createDatabaseBackupInfo() {
        return String.format("""
            Database Backup Information
            ==========================
            Backup Date: %s
            Database: AutoDetail
            Tables:
            - items
            - suppliers
            - categories
            - users
            - clients
            - orders
            - carts
            
            Note: This is a placeholder for actual database dump.
            In production, use mysqldump or pg_dump for real database backup.
            """, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    /**
     * Создание директорий для бэкапов
     */
    private void createBackupDirectories() throws IOException {
        Files.createDirectories(Paths.get(BACKUP_DIR));
        Files.createDirectories(Paths.get(DB_BACKUP_DIR));
    }

    /**
     * Очистка старых бэкапов (оставляем только последние 4 недели)
     */
    private void cleanupOldBackups() {
        try {
            File backupDir = new File(BACKUP_DIR);
            File dbBackupDir = new File(DB_BACKUP_DIR);

            // Удаляем файлы старше 30 дней
            deleteOldFiles(backupDir, 30);
            deleteOldFiles(dbBackupDir, 30);

            System.out.println("✅ Очистка старых бэкапов завершена");

        } catch (Exception e) {
            System.err.println("⚠️ Ошибка при очистке старых бэкапов: " + e.getMessage());
        }
    }

    /**
     * Удаление файлов старше указанного количества дней
     */
    private void deleteOldFiles(File directory, int daysOld) {
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }

        long cutoff = System.currentTimeMillis() - (daysOld * 24L * 60 * 60 * 1000);

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.lastModified() < cutoff) {
                    if (file.delete()) {
                        System.out.println("🗑️ Удален старый бэкап: " + file.getName());
                    } else {
                        System.err.println("⚠️ Не удалось удалить: " + file.getName());
                    }
                }
            }
        }
    }

    /**
     * Получение сервиса экспорта (заглушка - в реальном приложении используй Dependency Injection)
     */
    private ExportImportService getExportService() {
        // В реальном приложении это должно быть внедрено через @Autowired
        // Здесь возвращаем заглушку для примера
        return new ExportImportService(null, null, null) {
            @Override
            public byte[] exportItemsToExcel() throws IOException {
                // Реальная реализация будет в основном сервисе
                return "Items backup data".getBytes();
            }

            @Override
            public byte[] exportSuppliersToExcel() throws IOException {
                return "Suppliers backup data".getBytes();
            }

            @Override
            public byte[] exportCategoriesToExcel() throws IOException {
                return "Categories backup data".getBytes();
            }
        };
    }

    /**
     * Ручной запуск резервного копирования (для тестирования)
     */
    public void manualBackup() {
        System.out.println("🔧 Запуск ручного резервного копирования...");
        performWeeklyBackup();
    }

    /**
     * Получение информации о последних бэкапах
     */
    public String getBackupInfo() {
        try {
            File backupDir = new File(BACKUP_DIR);
            File dbBackupDir = new File(DB_BACKUP_DIR);

            StringBuilder info = new StringBuilder();
            info.append("Backup Information\n");
            info.append("==================\n");

            info.append("Application Backups:\n");
            if (backupDir.exists()) {
                File[] backups = backupDir.listFiles();
                if (backups != null && backups.length > 0) {
                    for (File backup : backups) {
                        info.append("- ").append(backup.getName())
                                .append(" (").append(new Date(backup.lastModified())).append(")\n");
                    }
                } else {
                    info.append("No application backups found\n");
                }
            }

            info.append("\nDatabase Backups:\n");
            if (dbBackupDir.exists()) {
                File[] dbBackups = dbBackupDir.listFiles();
                if (dbBackups != null && dbBackups.length > 0) {
                    for (File backup : dbBackups) {
                        info.append("- ").append(backup.getName())
                                .append(" (").append(new Date(backup.lastModified())).append(")\n");
                    }
                } else {
                    info.append("No database backups found\n");
                }
            }

            return info.toString();

        } catch (Exception e) {
            return "Error reading backup information: " + e.getMessage();
        }
    }
}