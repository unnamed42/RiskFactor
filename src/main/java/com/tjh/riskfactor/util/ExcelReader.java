package com.tjh.riskfactor.util;

import lombok.AllArgsConstructor;
import lombok.Value;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import org.springframework.lang.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 解析医院提供的excel格式数据
 * 该reader为按行遍历，每行亦可遍历
 * 按照医院提供的的数据，第一行为一级标题（合并单元格），第二行为二级标题（合并单元格），第三行为问题标题（正常单元格），第四行开始为回答内容
 *
 * 实现时需要注意，{@code Sheet::getLastRowNum()}是最后一行的下标，{@code Row::getLastCellNum()}是行中最后一列的下标+1
 */
public class ExcelReader implements Closeable {

    private final Sheet sheet;
    private final TreeMap<Integer, CellRangeAddress> mergedRegions = new TreeMap<>();

    public ExcelReader(InputStream is) throws IOException {
        var document = WorkbookFactory.create(is);
        sheet = document.getSheetAt(0);
        for(var range : sheet.getMergedRegions())
            mergedRegions.put(rowCol(range), range);
    }

    /**
     * 获取行列所在合并单元格的Cell
     * @param row 行index
     * @param col 列index
     * @return Cell
     */
    Optional<Cell> getMergedCell(int row, int col) {
        var floor = mergedRegions.floorEntry(rowCol(row, col));
        assert floor.getValue().isInRange(row, col);
        int row_ = floor.getValue().getFirstRow(), col_ = floor.getValue().getFirstColumn();
        return getCell(row_, col_);
    }

    Optional<Cell> getCell(int row, int col) {
        return Optional.ofNullable(sheet.getRow(row))
                .flatMap(r -> Optional.ofNullable(r.getCell(col)));
    }

    private int rowCol(CellRangeAddress range) {
        return rowCol(range.getFirstRow(), range.getFirstColumn());
    }

    private int rowCol(int row, int col) {
        int cols = sheet.getRow(2).getLastCellNum();
        return row * cols + col;
    }

    public Stream<Stream<DataCell>> stream() {
        if(sheet.getLastRowNum() < 3)
            return Stream.empty();
        return IntStream.rangeClosed(3, sheet.getLastRowNum()).mapToObj(r -> {
            var row = sheet.getRow(r);
            return IntStream.range(0, row.getLastCellNum()).mapToObj(col -> {
                var h1 = getMergedCell(0, col).map(Cell::getStringCellValue).get();
                var h2 = getMergedCell(1, col).map(Cell::getStringCellValue).orElse(null);
                var h3 = getCell(2, col).map(Cell::getStringCellValue).get();
                return new DataCell(h1, h2, h3, getCell(r, col).orElse(null));
            });
        });
    }

    public Stream<DataCell> cells() {
        return stream().flatMap(s -> s);
    }

    @Override
    public void close() throws IOException {
        sheet.getWorkbook().close();
    }

    @AllArgsConstructor
    public static class DataCell {
        public final String headerL1;
        @Nullable public final String headerL2;
        public final String header;
        @Nullable public final Cell cell;
    }

}

