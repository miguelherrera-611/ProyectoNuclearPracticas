import { jsPDF } from 'jspdf'
import type { InstanciaPracticaResponseV2, PazYSalvoResponse } from '../types'
import { formatFecha, formatFechaHora } from './format'

const COLOR_PRIMARY = [26, 54, 93] as const
const COLOR_ACCENT = [49, 130, 206] as const
const COLOR_LIGHT = [235, 248, 255] as const
const COLOR_SUCCESS = [39, 103, 73] as const
const COLOR_DANGER = [155, 44, 44] as const
const COLOR_TEXT = [45, 55, 72] as const
const COLOR_MUTED = [113, 128, 150] as const

const MARGIN_X = 18
const PAGE_WIDTH = 210

function extraerNotaFinal(contenido: string): string | null {
  const match = contenido.match(/Nota final:\s*([\d.,]+)/i)
  return match ? match[1] : null
}

interface FilaInfo {
  etiqueta: string
  valor: string
  colorValor?: readonly [number, number, number]
}

/** Construye y descarga el PDF oficial de paz y salvo a partir de los datos de la práctica. */
export function descargarPazYSalvoPdf(practica: InstanciaPracticaResponseV2, pazYSalvo: PazYSalvoResponse): void {
  const doc = new jsPDF({ unit: 'mm', format: 'a4' })

  // ── Encabezado institucional ──────────────────────────────────────────
  doc.setFillColor(...COLOR_PRIMARY)
  doc.rect(0, 0, PAGE_WIDTH, 30, 'F')
  doc.setTextColor(255, 255, 255)
  doc.setFont('helvetica', 'bold')
  doc.setFontSize(15)
  doc.text('UNIVERSIDAD ALEXANDER VON HUMBOLDT', PAGE_WIDTH / 2, 15, { align: 'center' })
  doc.setFont('helvetica', 'normal')
  doc.setFontSize(10)
  doc.text('Sistema de Gestión de Prácticas Empresariales', PAGE_WIDTH / 2, 22, { align: 'center' })

  // ── Título del documento ──────────────────────────────────────────────
  doc.setTextColor(...COLOR_PRIMARY)
  doc.setFont('helvetica', 'bold')
  doc.setFontSize(16)
  doc.text('PAZ Y SALVO — PRÁCTICA EMPRESARIAL', PAGE_WIDTH / 2, 42, { align: 'center' })

  doc.setDrawColor(...COLOR_ACCENT)
  doc.setLineWidth(0.6)
  doc.line(MARGIN_X, 47, PAGE_WIDTH - MARGIN_X, 47)

  // ── Párrafo introductorio ──────────────────────────────────────────────
  doc.setTextColor(...COLOR_TEXT)
  doc.setFont('helvetica', 'normal')
  doc.setFontSize(10.5)
  const intro =
    'La Coordinación de Prácticas Empresariales de la Universidad Alexander Von Humboldt certifica ' +
    'que el (la) estudiante registrado(a) a continuación finalizó su práctica empresarial y se encuentra ' +
    'a paz y salvo por todo concepto académico y administrativo relacionado con dicho proceso:'
  const introLines = doc.splitTextToSize(intro, PAGE_WIDTH - MARGIN_X * 2)
  doc.text(introLines, MARGIN_X, 56)

  // ── Cuadro de datos de la práctica ─────────────────────────────────────
  const notaFinal = extraerNotaFinal(pazYSalvo.contenido)
  const filas: FilaInfo[] = [
    { etiqueta: 'Estudiante', valor: practica.nombreEstudiante ?? '—' },
    { etiqueta: 'Práctica', valor: `${practica.nombre} (No. ${practica.numeroPractica})` },
    { etiqueta: 'Materia núcleo', valor: `${practica.materiaNucleo} (${practica.codigoMateria})` },
    { etiqueta: 'Empresa', valor: practica.razonSocialEmpresa ?? '—' },
    { etiqueta: 'Docente asesor', valor: practica.nombreDocenteAsesor ?? '—' },
    { etiqueta: 'Tutor empresarial', valor: practica.nombreTutorEmpresarial ?? '—' },
    { etiqueta: 'Duración', valor: `${practica.duracionSemanas} semanas` },
    { etiqueta: 'Fecha de inicio', valor: formatFecha(practica.fechaInicio) },
    { etiqueta: 'Fecha de finalización', valor: formatFecha(practica.fechaFin) },
    {
      etiqueta: 'Resultado final',
      valor: practica.resultadoCierre?.replace(/_/g, ' ') ?? '—',
      colorValor: practica.resultadoCierre === 'APROBADO' ? COLOR_SUCCESS : COLOR_DANGER,
    },
    ...(notaFinal ? [{ etiqueta: 'Nota final', valor: notaFinal }] : []),
    { etiqueta: 'Fecha de cierre', valor: formatFechaHora(practica.fechaCierre) },
  ]

  const boxTop = 72
  const rowHeight = 7.2
  const boxHeight = filas.length * rowHeight + 8
  doc.setFillColor(...COLOR_LIGHT)
  doc.roundedRect(MARGIN_X, boxTop, PAGE_WIDTH - MARGIN_X * 2, boxHeight, 2, 2, 'F')

  let y = boxTop + 8
  const labelX = MARGIN_X + 6
  const valueX = MARGIN_X + 58
  filas.forEach(fila => {
    doc.setFont('helvetica', 'bold')
    doc.setFontSize(10)
    doc.setTextColor(...COLOR_PRIMARY)
    doc.text(fila.etiqueta, labelX, y)

    doc.setFont('helvetica', 'normal')
    doc.setTextColor(...(fila.colorValor ?? COLOR_TEXT))
    doc.text(fila.valor, valueX, y)
    y += rowHeight
  })

  // ── Párrafo de cierre ───────────────────────────────────────────────────
  doc.setTextColor(...COLOR_TEXT)
  doc.setFont('helvetica', 'normal')
  doc.setFontSize(10.5)
  const cierre =
    'En virtud de lo anterior, se hace constar que el (la) estudiante no tiene pendiente alguno —académico, ' +
    'documental ni administrativo— derivado de su proceso de práctica empresarial ante esta universidad.'
  const cierreLines = doc.splitTextToSize(cierre, PAGE_WIDTH - MARGIN_X * 2)
  doc.text(cierreLines, MARGIN_X, boxTop + boxHeight + 12)

  // ── Firma ────────────────────────────────────────────────────────────────
  const firmaY = boxTop + boxHeight + 45
  doc.setDrawColor(...COLOR_MUTED)
  doc.setLineWidth(0.3)
  doc.line(PAGE_WIDTH / 2 - 35, firmaY, PAGE_WIDTH / 2 + 35, firmaY)
  doc.setFontSize(9.5)
  doc.setTextColor(...COLOR_TEXT)
  doc.text('Coordinación de Prácticas Empresariales', PAGE_WIDTH / 2, firmaY + 5, { align: 'center' })

  // ── Pie de página ────────────────────────────────────────────────────────
  const footerY = 280
  doc.setDrawColor(...COLOR_ACCENT)
  doc.setLineWidth(0.3)
  doc.line(MARGIN_X, footerY - 6, PAGE_WIDTH - MARGIN_X, footerY - 6)
  doc.setFont('helvetica', 'normal')
  doc.setFontSize(8.5)
  doc.setTextColor(...COLOR_MUTED)
  doc.text(`Código de verificación: ${pazYSalvo.codigo}`, MARGIN_X, footerY)
  doc.text(`Generado el: ${formatFechaHora(pazYSalvo.generadoEn)}`, PAGE_WIDTH - MARGIN_X, footerY, { align: 'right' })
  doc.setFontSize(8)
  doc.text(
    'Documento generado automáticamente por el Sistema de Gestión de Prácticas Empresariales.',
    PAGE_WIDTH / 2, footerY + 6, { align: 'center' },
  )

  doc.save(`Paz_y_Salvo_${pazYSalvo.codigo}.pdf`)
}
