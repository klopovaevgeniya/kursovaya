package com.example.AutoDetail.service;

import com.example.AutoDetail.entity.*;
import com.example.AutoDetail.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class ExportImportService {

    private final ItemRepository itemRepository;
    private final SupplierRepository supplierRepository;
    private final CategoryRepository categoryRepository;

    public ExportImportService(ItemRepository itemRepository,
                               SupplierRepository supplierRepository,
                               CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.supplierRepository = supplierRepository;
        this.categoryRepository = categoryRepository;
    }

    // === ЭКСПОРТ ===

    public byte[] exportItemsToExcel() throws IOException {
        List<Item> items = itemRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Товары");

            // Заголовки
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Артикул", "Название", "Цена", "Количество", "Категория", "Поставщик", "Изображение"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Данные
            int rowNum = 1;
            for (Item item : items) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getId());
                row.createCell(1).setCellValue(item.getArctical());
                row.createCell(2).setCellValue(item.getName());
                row.createCell(3).setCellValue(item.getPrice() != null ? item.getPrice() : 0.0);
                row.createCell(4).setCellValue(item.getQuantity());
                row.createCell(5).setCellValue(item.getCategory() != null ? item.getCategory().getName() : "");
                row.createCell(6).setCellValue(item.getSupplier() != null ? item.getSupplier().getName() : "");
                row.createCell(7).setCellValue(item.getImage());
            }

            // Авто-размер колонок
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] exportSuppliersToExcel() throws IOException {
        List<Supplier> suppliers = supplierRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Поставщики");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Название компании", "Контактный телефон", "Email"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Supplier supplier : suppliers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(supplier.getId());
                row.createCell(1).setCellValue(supplier.getName());
                row.createCell(2).setCellValue(supplier.getContactPhone());
                row.createCell(3).setCellValue(supplier.getContactEmail() != null ? supplier.getContactEmail() : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] exportCategoriesToExcel() throws IOException {
        List<Category> categories = categoryRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Категории");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Название", "Описание"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Category category : categories) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(category.getId());
                row.createCell(1).setCellValue(category.getName());
                row.createCell(2).setCellValue(category.getDescription() != null ? category.getDescription() : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    // === ИМПОРТ ===

    public String importData(MultipartFile file, String dataType) throws IOException {
        int importedCount = 0;

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            switch (dataType) {
                case "items":
                    importedCount = importItems(sheet);
                    break;
                case "suppliers":
                    importedCount = importSuppliers(sheet);
                    break;
                case "categories":
                    importedCount = importCategories(sheet);
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный тип данных: " + dataType);
            }
        }

        return "Успешно импортировано " + importedCount + " записей";
    }

    private int importItems(Sheet sheet) {
        int count = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                Item item = new Item();
                item.setArctical(getStringCellValue(row.getCell(1)));
                item.setName(getStringCellValue(row.getCell(2)));
                item.setPrice(getDoubleCellValue(row.getCell(3))); // Исправлено на Double
                item.setQuantity(getIntCellValue(row.getCell(4)));

                // Поиск категории по имени
                String categoryName = getStringCellValue(row.getCell(5));
                if (!categoryName.isEmpty()) {
                    categoryRepository.findByName(categoryName)
                            .ifPresent(item::setCategory);
                }

                // Поиск поставщика по имени
                String supplierName = getStringCellValue(row.getCell(6));
                if (!supplierName.isEmpty()) {
                    supplierRepository.findByName(supplierName)
                            .ifPresent(item::setSupplier);
                }

                item.setImage(getStringCellValue(row.getCell(7)));
                itemRepository.save(item);
                count++;
            } catch (Exception e) {
                System.err.println("Ошибка импорта товара в строке " + (i + 1) + ": " + e.getMessage());
            }
        }
        return count;
    }

    private int importSuppliers(Sheet sheet) {
        int count = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                Supplier supplier = new Supplier();
                supplier.setName(getStringCellValue(row.getCell(1)));
                supplier.setContactPhone(getStringCellValue(row.getCell(2)));
                supplier.setContactEmail(getStringCellValue(row.getCell(3)));

                supplierRepository.save(supplier);
                count++;
            } catch (Exception e) {
                System.err.println("Ошибка импорта поставщика в строке " + (i + 1) + ": " + e.getMessage());
            }
        }
        return count;
    }

    private int importCategories(Sheet sheet) {
        int count = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                Category category = new Category();
                category.setName(getStringCellValue(row.getCell(1)));
                category.setDescription(getStringCellValue(row.getCell(2)));

                categoryRepository.save(category);
                count++;
            } catch (Exception e) {
                System.err.println("Ошибка импорта категории в строке " + (i + 1) + ": " + e.getMessage());
            }
        }
        return count;
    }

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===

    private String getStringCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC:
                // Для числовых значений, которые должны быть строками (например, телефон)
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Преобразуем целые числа без десятичных знаков
                    double numValue = cell.getNumericCellValue();
                    if (numValue == Math.floor(numValue)) {
                        return String.valueOf((long) numValue);
                    } else {
                        return String.valueOf(numValue);
                    }
                }
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception ex) {
                        return String.valueOf(cell.getBooleanCellValue());
                    }
                }
            default: return "";
        }
    }

    private Double getDoubleCellValue(Cell cell) {
        if (cell == null) return 0.0;
        try {
            return cell.getNumericCellValue();
        } catch (Exception e) {
            // Пробуем преобразовать из строки
            try {
                String stringValue = getStringCellValue(cell);
                if (!stringValue.isEmpty()) {
                    return Double.parseDouble(stringValue.replace(",", "."));
                }
            } catch (Exception ex) {
                // Игнорируем ошибки преобразования
            }
            return 0.0;
        }
    }

    private int getIntCellValue(Cell cell) {
        if (cell == null) return 0;
        try {
            return (int) cell.getNumericCellValue();
        } catch (Exception e) {
            // Пробуем преобразовать из строки
            try {
                String stringValue = getStringCellValue(cell);
                if (!stringValue.isEmpty()) {
                    return Integer.parseInt(stringValue);
                }
            } catch (Exception ex) {
                // Игнорируем ошибки преобразования
            }
            return 0;
        }
    }
}