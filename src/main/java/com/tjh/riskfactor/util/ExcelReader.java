package com.tjh.riskfactor.util;

import lombok.Value;

import org.apache.poi.ss.usermodel.*;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

public class ExcelReader implements Closeable, Iterable<ExcelReader.Entry> {

    private final Workbook document;
    private final Sheet sheet;

    @Value
    public static class Entry {
        private String parentHeader;
        private String childHeader;
        private String cellHeader;
        private Cell cell;
    }

    public class EntryIterator implements Iterator<Entry> {
        private int row = 3;
        private int col = 0;

        private String content(Optional<Cell> oc) {
            return oc.map(Cell::getStringCellValue).orElse("null");
        }

        @Override
        public boolean hasNext() {
            return row <= sheet.getLastRowNum();
        }

        @Override
        public Entry next() {
            var pHeader = content(getMergedCell(0, col));
            var cHeader = content(getMergedCell(1, col));
            var tHeader = content(getCell(2, col));
            var cell = sheet.getRow(row).getCell(col);

            var entry = new Entry(pHeader, cHeader, tHeader, cell);

            if(++col > sheet.getRow(row).getLastCellNum()) {
                ++row; col = 0;
            }

            return entry;
        }
    }

    public ExcelReader(InputStream is) throws IOException {
        document = WorkbookFactory.create(is);
        sheet = document.getSheetAt(0);
    }

    public Stream<Entry> stream() {
        var iterator = this.iterator();
        return Stream.generate(() -> null).takeWhile(x -> iterator.hasNext())
                .map(x -> iterator.next());
    }

    private Optional<Cell> getMergedCell(int row, int col) {
        int mergedIdx = 0, mergedTotal = sheet.getNumMergedRegions();
        for(; mergedIdx <= mergedTotal; ++mergedIdx) {
            var range = sheet.getMergedRegion(mergedIdx);
            if(range.isInRange(row, col))
                break;
        }
        if(mergedIdx > mergedTotal)
            return Optional.empty();
        var range = sheet.getMergedRegion(mergedIdx);
        return getCell(range.getFirstRow(), range.getFirstColumn());
    }

    private Optional<Cell> getCell(int row, int col) {
        return Optional.ofNullable(sheet.getRow(row))
                .flatMap(r -> Optional.ofNullable(r.getCell(col)));
    }

    @Override
    public EntryIterator iterator() {
        return new EntryIterator();
    }

    @Override
    public void close() throws IOException {
        document.close();
    }
}
