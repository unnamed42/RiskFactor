package com.tjh.riskfactor.util

import mu.KotlinLogging
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress

import java.io.Closeable
import java.io.InputStream
import java.util.TreeMap

private fun rowCol(sheet: Sheet, range: CellRangeAddress) =
    rowCol(sheet, range.firstRow, range.firstColumn)

private val logger = KotlinLogging.logger {}

private fun rowCol(sheet: Sheet, row: Int, col: Int) =
    (sheet.getRow(2).lastCellNum) * row + col

class ExcelReader(istream: InputStream) : Closeable {

    private val sheet: Sheet
    private val mergedRegions = TreeMap<Int, CellRangeAddress>()

    init {
        val document = WorkbookFactory.create(istream)
        sheet = document.getSheetAt(0)
        for (range in sheet.mergedRegions)
            mergedRegions[rowCol(sheet, range)] = range
    }

    fun cells() = sequence {
        if(sheet.lastRowNum < 3) {
            for (rowIdx in 3 .. sheet.lastRowNum) {
                val row = sheet.getRow(rowIdx)
                for(colIdx in 0 until row.lastCellNum) {
                    val h1Cell = getMergedCell(0, colIdx)
                    val h2Cell = getMergedCell(1, colIdx)
                    val h3Cell = getCell(2, colIdx)
                    if(h1Cell == null)
                        logger.error { "header 1 cell at col $colIdx is null" }
                    if(h3Cell == null)
                        logger.error { "header 3 cell at col $colIdx is null" }

                    yield(DataCell(
                        headerL1 = h1Cell!!.stringCellValue,
                        headerL2 = h2Cell?.stringCellValue,
                        header = h3Cell!!.stringCellValue,
                        cell = getCell(rowIdx, colIdx)
                    ))
                }
            }
        }
    }

    /**
     * 获取行列所在合并单元格的Cell
     * @param row 行index
     * @param col 列index
     * @return Cell
     */
    private fun getMergedCell(row: Int, col: Int): Cell? {
        val floorEntry = mergedRegions.floorEntry(rowCol(sheet, row, col))
        val (_, value) = floorEntry; assert(value.isInRange(row, col))
        return getCell(value.firstRow, value.firstColumn)
    }

    private fun getCell(row: Int, col: Int): Cell? =
        sheet.getRow(row)?.getCell(col)

    override fun close() {
        sheet.workbook.close()
    }

    data class DataCell(
        val headerL1: String,
        val headerL2: String?,
        val header: String,
        val cell: Cell?
    )
}
