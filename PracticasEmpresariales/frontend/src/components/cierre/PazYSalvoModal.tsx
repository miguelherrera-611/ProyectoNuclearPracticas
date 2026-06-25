import { useEffect, useState } from 'react'
import { Modal } from '../common/Modal/Modal'
import { Button } from '../common/Button/Button'
import { sprint4Service } from '../../services/sprint4Service'
import { descargarPazYSalvoPdf } from '../../utils/pazYSalvoPdf'
import { formatFecha, formatFechaHora, extractApiError } from '../../utils/format'
import type { InstanciaPracticaResponseV2, PazYSalvoResponse } from '../../types'

interface PazYSalvoModalProps {
  practica: InstanciaPracticaResponseV2
  onClose: () => void
}

export function PazYSalvoModal({ practica, onClose }: PazYSalvoModalProps) {
  const [pazYSalvo, setPazYSalvo] = useState<PazYSalvoResponse | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    sprint4Service.pazYSalvo(practica.id)
      .then(setPazYSalvo)
      .catch((err: unknown) => setError(extractApiError(err, 'No se pudo cargar el paz y salvo de esta práctica.')))
      .finally(() => setLoading(false))
  }, [practica.id])

  const descargar = () => {
    if (!pazYSalvo) return
    descargarPazYSalvoPdf(practica, pazYSalvo)
  }

  return (
    <Modal title="Detalle de mi práctica" subtitle={practica.nombre} onClose={onClose} size="lg">
      <div className="space-y-5">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-sm text-gray-600">
          <div><span className="font-medium text-gray-700">Empresa:</span> {practica.razonSocialEmpresa ?? '—'}</div>
          <div><span className="font-medium text-gray-700">Docente asesor:</span> {practica.nombreDocenteAsesor ?? '—'}</div>
          <div><span className="font-medium text-gray-700">Tutor empresarial:</span> {practica.nombreTutorEmpresarial ?? '—'}</div>
          <div><span className="font-medium text-gray-700">Duración:</span> {practica.duracionSemanas} semanas</div>
          {practica.fechaInicio && <div><span className="font-medium text-gray-700">Inicio:</span> {formatFecha(practica.fechaInicio)}</div>}
          {practica.fechaFin && <div><span className="font-medium text-gray-700">Fin:</span> {formatFecha(practica.fechaFin)}</div>}
          {practica.fechaCierre && <div><span className="font-medium text-gray-700">Cierre:</span> {formatFechaHora(practica.fechaCierre)}</div>}
          {practica.resultadoCierre && (
            <div>
              <span className="font-medium text-gray-700">Resultado:</span>{' '}
              <span className={practica.resultadoCierre === 'APROBADO' ? 'text-green-600 font-medium' : 'text-red-600 font-medium'}>
                {practica.resultadoCierre.replace(/_/g, ' ')}
              </span>
            </div>
          )}
        </div>

        {loading && <div className="text-sm text-gray-400">Cargando paz y salvo...</div>}

        {!loading && error && (
          <div className="card border-amber-200 bg-amber-50 text-amber-800 text-sm">{error}</div>
        )}

        {!loading && pazYSalvo && (
          <div className="rounded-lg border border-green-200 bg-green-50 p-4 space-y-3">
            <p className="text-sm text-green-800">
              Tu práctica ha finalizado y tu paz y salvo ha sido generado con satisfacción.
            </p>
            <p className="text-xs text-green-700">
              Código: <strong>{pazYSalvo.codigo}</strong> · Generado el {formatFechaHora(pazYSalvo.generadoEn)}
            </p>
            <Button onClick={descargar} className="w-full">Descargar paz y salvo (PDF)</Button>
          </div>
        )}
      </div>
    </Modal>
  )
}
