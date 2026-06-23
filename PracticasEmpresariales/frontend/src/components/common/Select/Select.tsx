import {
  Children,
  isValidElement,
  KeyboardEvent,
  ReactNode,
  SelectHTMLAttributes,
  useEffect,
  useId,
  useMemo,
  useRef,
  useState,
} from 'react'

interface SelectProps extends SelectHTMLAttributes<HTMLSelectElement> {
  label?: string
  error?: string
  hint?: string
}

interface OptionData {
  value: string
  label: string
}

function getOptions(children: ReactNode): OptionData[] {
  const options: OptionData[] = []
  Children.forEach(children, child => {
    if (isValidElement(child) && child.type === 'option') {
      const props = child.props as { value?: string | number; children?: ReactNode }
      options.push({
        value: props.value !== undefined ? String(props.value) : '',
        label: Children.toArray(props.children).join(''),
      })
    }
  })
  return options
}

export function Select({
  label,
  error,
  hint,
  className = '',
  children,
  value,
  onChange,
  disabled,
  required,
  name,
  id,
}: SelectProps) {
  const [open, setOpen] = useState(false)
  const [highlighted, setHighlighted] = useState(0)
  const containerRef = useRef<HTMLDivElement>(null)
  const listboxId = useId()

  const options = useMemo(() => getOptions(children), [children])
  const currentValue = value !== undefined ? String(value) : ''
  const selected = options.find(o => o.value === currentValue)

  useEffect(() => {
    if (!open) return
    const handleOutsideClick = (e: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setOpen(false)
      }
    }
    document.addEventListener('mousedown', handleOutsideClick)
    return () => document.removeEventListener('mousedown', handleOutsideClick)
  }, [open])

  const openDropdown = () => {
    if (disabled) return
    const idx = options.findIndex(o => o.value === currentValue)
    setHighlighted(idx >= 0 ? idx : 0)
    setOpen(true)
  }

  const selectOption = (option: OptionData) => {
    setOpen(false)
    if (option.value === currentValue) return
    onChange?.({ target: { value: option.value, name, id } } as unknown as Parameters<NonNullable<typeof onChange>>[0])
  }

  const handleTriggerKeyDown = (e: KeyboardEvent<HTMLButtonElement>) => {
    if (disabled) return
    if (!open) {
      if (['ArrowDown', 'ArrowUp', 'Enter', ' '].includes(e.key)) {
        e.preventDefault()
        openDropdown()
      }
      return
    }
    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault()
        setHighlighted(h => Math.min(h + 1, options.length - 1))
        break
      case 'ArrowUp':
        e.preventDefault()
        setHighlighted(h => Math.max(h - 1, 0))
        break
      case 'Home':
        e.preventDefault()
        setHighlighted(0)
        break
      case 'End':
        e.preventDefault()
        setHighlighted(options.length - 1)
        break
      case 'Enter':
      case ' ':
        e.preventDefault()
        if (options[highlighted]) selectOption(options[highlighted])
        break
      case 'Escape':
        e.preventDefault()
        setOpen(false)
        break
      case 'Tab':
        setOpen(false)
        break
    }
  }

  return (
    <div className="space-y-1" ref={containerRef}>
      {label && (
        <label className="block text-sm font-medium text-gray-700">
          {label}
          {required && <span className="text-red-500 ml-0.5">*</span>}
        </label>
      )}
      <div className="relative">
        <button
          type="button"
          id={id}
          disabled={disabled}
          role="combobox"
          aria-haspopup="listbox"
          aria-expanded={open}
          aria-controls={listboxId}
          aria-required={required}
          onClick={() => (open ? setOpen(false) : openDropdown())}
          onKeyDown={handleTriggerKeyDown}
          className={`w-full flex items-center justify-between gap-2 bg-white border rounded-lg px-3 py-2 text-sm text-left text-gray-900 transition-colors disabled:bg-gray-50 disabled:text-gray-400 disabled:cursor-not-allowed ${
            disabled ? '' : 'cursor-pointer'
          } focus:outline-none focus:ring-2 focus:ring-cue-accent focus:border-transparent ${
            error
              ? 'border-red-400 bg-red-50'
              : open
                ? 'border-cue-accent ring-2 ring-cue-accent/30'
                : 'border-gray-300 hover:border-gray-400'
          } ${className}`}
        >
          <span className="truncate">{selected ? selected.label : ''}</span>
          <svg
            className={`w-4 h-4 shrink-0 transition-transform duration-150 ${error ? 'text-red-400' : 'text-gray-400'} ${
              open ? 'rotate-180' : ''
            }`}
            viewBox="0 0 20 20"
            fill="currentColor"
          >
            <path
              fillRule="evenodd"
              d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z"
              clipRule="evenodd"
            />
          </svg>
        </button>

        {open && (
          <ul
            id={listboxId}
            role="listbox"
            tabIndex={-1}
            aria-activedescendant={options[highlighted] ? `${listboxId}-${highlighted}` : undefined}
            className="absolute z-20 mt-1.5 w-full max-h-60 overflow-auto rounded-lg border border-gray-200 bg-white py-1 shadow-lg shadow-gray-200/60 ring-1 ring-black/5 focus:outline-none"
          >
            {options.length === 0 && (
              <li className="px-3 py-2 text-sm text-gray-400">Sin opciones</li>
            )}
            {options.map((option, idx) => {
              const isSelected = option.value === currentValue
              const isHighlighted = idx === highlighted
              return (
                <li
                  key={`${option.value}-${idx}`}
                  id={`${listboxId}-${idx}`}
                  role="option"
                  aria-selected={isSelected}
                  onMouseEnter={() => setHighlighted(idx)}
                  onMouseDown={e => e.preventDefault()}
                  onClick={() => selectOption(option)}
                  className={`mx-1 flex items-center justify-between gap-2 rounded-md px-3 py-2 text-sm cursor-pointer select-none transition-colors ${
                    isHighlighted ? 'bg-cue-light' : ''
                  } ${isSelected ? 'text-cue-primary font-medium' : 'text-gray-700'}`}
                >
                  <span className="truncate">{option.label}</span>
                  {isSelected && (
                    <svg className="w-4 h-4 shrink-0 text-cue-accent" viewBox="0 0 20 20" fill="currentColor">
                      <path
                        fillRule="evenodd"
                        d="M16.704 5.29a1 1 0 010 1.42l-7.5 7.5a1 1 0 01-1.42 0l-3.5-3.5a1 1 0 111.42-1.42l2.79 2.79 6.79-6.79a1 1 0 011.42 0z"
                        clipRule="evenodd"
                      />
                    </svg>
                  )}
                </li>
              )
            })}
          </ul>
        )}
      </div>
      {error && <p className="text-xs text-red-600">{error}</p>}
      {hint && !error && <p className="text-xs text-gray-400">{hint}</p>}
    </div>
  )
}
