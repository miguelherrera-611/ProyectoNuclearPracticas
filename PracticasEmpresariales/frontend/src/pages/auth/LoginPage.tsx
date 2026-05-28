import { useState, FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

// Credenciales de prueba sembradas por DataInitializer al arrancar el backend.
// Hacer clic en una fila autocompleta el formulario de login.
const USUARIOS_PRUEBA = [
  { rol: 'Admin DTI',             correo: 'dti@cue.edu.co',           password: 'Admin2026!' },
  { rol: 'Coordinación Académica',correo: 'coordinacion@cue.edu.co',  password: 'Coord2026!' },
  { rol: 'Coordinador Prácticas', correo: 'practicas@cue.edu.co',     password: 'Pract2026!' },
  { rol: 'Docente Asesor',        correo: 'docente@cue.edu.co',       password: 'Docente2026!' },
  { rol: 'Tutor Empresarial',     correo: 'tutor@empresa.com',        password: 'Tutor2026!' },
  { rol: 'Estudiante',            correo: 'estudiante@cue.edu.co',    password: 'Estud2026!' },
  { rol: 'Dirección',             correo: 'direccion@cue.edu.co',     password: 'Direc2026!' },
]

export default function LoginPage() {
  const { login, loading } = useAuth()
  const navigate = useNavigate()
  const [correo, setCorreo] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  // Autocompletar el formulario con las credenciales del usuario seleccionado
  const seleccionarUsuario = (u: typeof USUARIOS_PRUEBA[0]) => {
    setCorreo(u.correo)
    setPassword(u.password)
    setError('')
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    try {
      await login(correo, password)
      navigate('/dashboard', { replace: true })
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { mensaje?: string } } })
        ?.response?.data?.mensaje
      setError(msg ?? 'Credenciales incorrectas o cuenta inactiva.')
    }
  }

  return (
    <div className="min-h-screen bg-cue-primary flex items-center justify-center p-4">
      <div className="w-full max-w-xl flex flex-col gap-4">

        {/* ── Tarjeta principal de login ─────────────────────────────── */}
        <div className="bg-white rounded-2xl shadow-2xl overflow-hidden">

          {/* Header */}
          <div className="bg-cue-primary px-8 py-8 text-center">
            <h1 className="text-2xl font-bold text-white">Sistema de Prácticas</h1>
            <p className="text-blue-300 text-sm mt-1">Universidad Alexander Von Humboldt</p>
          </div>

          {/* Form */}
          <div className="px-8 py-8">
            <h2 className="text-xl font-semibold text-gray-800 mb-6">Iniciar sesión</h2>

            {error && (
              <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 mb-4 text-sm">
                {error}
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Correo electrónico
                </label>
                <input
                  type="email"
                  value={correo}
                  onChange={(e) => setCorreo(e.target.value)}
                  className="input-field"
                  placeholder="usuario@cue.edu.co"
                  required
                  autoComplete="email"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Contraseña
                </label>
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="input-field"
                  placeholder="••••••••"
                  required
                  autoComplete="current-password"
                />
              </div>

              <button
                type="submit"
                disabled={loading}
                className="w-full btn-primary py-3 flex items-center justify-center"
              >
                {loading ? (
                  <span className="animate-spin mr-2">⟳</span>
                ) : null}
                {loading ? 'Verificando...' : 'Ingresar'}
              </button>
            </form>

            <p className="text-xs text-gray-400 mt-6 text-center">
              Si no recuerdas tu contraseña, contacta al Administrador DTI.
            </p>
          </div>
        </div>

        {/* ── Panel de usuarios de prueba ────────────────────────────── */}
        {/* Visible solo en desarrollo; haz clic en cualquier fila para autocompletar */}
        <div className="bg-white rounded-2xl shadow-lg overflow-hidden">
          <div className="bg-gray-50 px-5 py-3 border-b border-gray-200">
            <p className="text-xs font-semibold text-gray-500 uppercase tracking-wide">
              Usuarios de prueba — haz clic para autocompletar
            </p>
          </div>
          <div className="divide-y divide-gray-100">
            {USUARIOS_PRUEBA.map((u) => (
              <button
                key={u.correo}
                type="button"
                onClick={() => seleccionarUsuario(u)}
                className="w-full text-left px-5 py-3 hover:bg-blue-50 transition-colors flex items-center justify-between group"
              >
                <div>
                  <span className="text-sm font-medium text-gray-800 group-hover:text-blue-700">
                    {u.rol}
                  </span>
                  <span className="block text-xs text-gray-400">{u.correo}</span>
                </div>
                <span className="text-xs text-gray-300 group-hover:text-blue-400 font-mono">
                  {u.password}
                </span>
              </button>
            ))}
          </div>
        </div>

      </div>
    </div>
  )
}
