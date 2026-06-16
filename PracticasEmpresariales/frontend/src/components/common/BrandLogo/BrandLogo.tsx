import dualityMark from '../../../assets/images/duality-mark.png'

type BrandLogoVariant = 'full' | 'compact' | 'sidebar'

interface BrandLogoProps {
  variant?: BrandLogoVariant
}

const variantStyles: Record<BrandLogoVariant, {
  wrapper: string
  image: string
  text: string
  title: string
  subtitle: string
}> = {
  full: {
    wrapper: 'flex flex-col items-center text-center',
    image: 'h-28 w-28 sm:h-32 sm:w-32 object-contain',
    text: 'mt-3',
    title: 'text-2xl sm:text-3xl font-extrabold italic tracking-normal',
    subtitle: 'text-xs sm:text-sm font-medium tracking-wide uppercase',
  },
  compact: {
    wrapper: 'flex items-center gap-3',
    image: 'h-10 w-10 object-contain',
    text: 'leading-tight',
    title: 'text-xl font-extrabold italic tracking-normal',
    subtitle: 'text-[11px] font-medium tracking-wide uppercase',
  },
  sidebar: {
    wrapper: 'flex items-center gap-3 min-w-0',
    image: 'h-12 w-12 object-contain rounded-lg bg-white p-1 shadow-sm',
    text: 'leading-tight min-w-0',
    title: 'text-xl font-extrabold italic tracking-normal',
    subtitle: 'text-[11px] font-medium tracking-wide uppercase truncate',
  },
}

export function BrandLogo({ variant = 'compact' }: BrandLogoProps) {
  const styles = variantStyles[variant]
  const titleColor = variant === 'sidebar' ? 'text-white' : 'text-cue-primary'
  const subtitleColor = variant === 'sidebar' ? 'text-blue-200' : 'text-cue-primary/70'

  return (
    <div className={`${styles.wrapper} brand-logo-motion`} aria-label="Duality, Sistema de Gestion de Practicas">
      <img
        src={dualityMark}
        alt="Duality"
        className={`${styles.image} brand-logo-mark`}
        draggable={false}
      />
      <div className={styles.text}>
        <p className={`${styles.title} ${titleColor} brand-logo-title`}>
          <span className="text-cue-danger">Dua</span>lity
        </p>
        <p className={`${styles.subtitle} ${subtitleColor}`}>Sistema de Gestion de Practicas</p>
      </div>
    </div>
  )
}
