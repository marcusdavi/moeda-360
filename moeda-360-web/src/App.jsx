import { useState } from 'react'
import { ArrowDown, ArrowLeftRight, ArrowRight, CalendarDays, Check, CircleDollarSign, LoaderCircle, RefreshCw, Sparkles } from 'lucide-react'

const apiUrl = import.meta.env.VITE_API_URL || ''
const yesterday = new Date(Date.now() - 86400000).toISOString().slice(0, 10)

const formatCurrency = (value, currency) =>
  new Intl.NumberFormat('pt-BR', { style: 'currency', currency }).format(value)

const formatInputCurrency = (value) => {
  const numericValue = Number(value.replace(/\./g, '').replace(',', '.'))
  if (!Number.isFinite(numericValue)) return value
  return numericValue.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const parseInputCurrency = (value) => Number(value.replace(/\./g, '').replace(',', '.'))

const currencySymbol = (currency) => currency === 'BRL' ? 'R$' : 'US$'

const formatDate = (value) => {
  if (!value) return 'Cotação atual'
  return new Intl.DateTimeFormat('pt-BR', { dateStyle: 'long' }).format(new Date(`${value}T12:00:00`))
}

const formatTime = (value) => {
  if (!value) return ''
  return new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(value))
}

function App() {
  const [amount, setAmount] = useState('100,00')
  const [currencyFrom, setCurrencyFrom] = useState('BRL')
  const [currencyTo, setCurrencyTo] = useState('USD')
  const [date, setDate] = useState('')
  const [result, setResult] = useState(null)
  const [history, setHistory] = useState([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  function handleSwap() {
    setCurrencyFrom(currencyTo)
    setCurrencyTo(currencyFrom)
    setResult(null)
    setError('')
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setResult(null)
    setLoading(true)

    try {
      const parsedAmount = parseInputCurrency(amount)
      if (!Number.isFinite(parsedAmount) || parsedAmount < 0.01) {
        throw new Error('Informe um valor maior que 0,01.')
      }

      const response = await fetch(`${apiUrl}/api/conversoes`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          valor: parsedAmount,
          moedaOrigem: currencyFrom,
          moedaDestino: currencyTo,
          ...(date ? { data: date } : {}),
        }),
      })
      const body = await response.json().catch(() => null)
      if (!response.ok) throw new Error(body?.detail || body?.message || 'Não foi possível consultar a cotação.')
      setResult(body)
      setHistory((currentHistory) => [body, ...currentHistory].slice(0, 5))
    } catch (requestError) {
      setError(requestError.message || 'Verifique se a API está em execução e tente novamente.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="app-shell">
      <header className="topbar">
        <a className="brand" href="/" aria-label="Moeda 360 início">
          <span className="brand-mark"><CircleDollarSign size={22} strokeWidth={2.3} /></span>
          <span>moeda<span>360</span></span>
        </a>
        <div className="live-status"><span className="status-dot" /> Cotação em tempo real</div>
      </header>

      <section className="hero">
        <div className="hero-copy">
          <p className="eyebrow"><Sparkles size={15} /> Clareza para suas decisões</p>
          <h1>Seu dinheiro,<br /><em>em outra moeda.</em></h1>
          <p className="intro">Converta entre reais e dólares com a cotação mais recente ou consulte um dia específico.</p>
        </div>

        <div className="conversion-layout">
          <form className="converter-card" onSubmit={handleSubmit}>
            <div className="card-heading">
              <div>
                <span className="section-label">Conversão</span>
                <h2>Quanto você quer converter?</h2>
              </div>
              <div className="pair-badge">
                <span>{currencyFrom}</span>
                <button className="swap-button" type="button" onClick={handleSwap} aria-label="Inverter moedas" title="Inverter moedas">
                  <ArrowLeftRight size={14} />
                </button>
                <span>{currencyTo}</span>
              </div>
            </div>

            <label className="field-label" htmlFor="amount">Valor em {currencyFrom === 'BRL' ? 'reais' : 'dólares'}</label>
            <div className="amount-field">
              <span>{currencySymbol(currencyFrom)}</span>
              <input
                id="amount"
                type="text"
                inputMode="decimal"
                pattern="[0-9.,]+"
                value={amount}
                onChange={(event) => setAmount(event.target.value.replace(/[^0-9,]/g, ''))}
                onBlur={() => setAmount(formatInputCurrency(amount))}
                required
              />
            </div>

            <label className="field-label date-label" htmlFor="date">
              Data da cotação <span>opcional</span>
            </label>
            <div className="date-field">
              <CalendarDays size={18} />
              <input id="date" type="date" max={yesterday} value={date} onChange={(event) => setDate(event.target.value)} />
            </div>
            <p className="field-hint">Sem data, usamos a cotação disponível agora.</p>

            {error && <div className="error-message" role="alert">{error}</div>}

            <button className="submit-button" type="submit" disabled={loading}>
              {loading ? <><LoaderCircle className="spin" size={19} /> Consultando...</> : <>Converter agora <ArrowRight size={19} /></>}
            </button>
          </form>

          <section className={`result-panel ${result ? 'has-result' : ''}`} aria-live="polite">
            {!result ? (
              <div className="empty-result">
                <div className="empty-icon"><ArrowDown size={22} /></div>
                <span className="section-label">Seu resultado aparece aqui</span>
                <p>Faça uma conversão para ver o valor final, a cotação usada e o momento da consulta.</p>
              </div>
            ) : (
              <div className="result-content">
                <div className="result-topline"><span className="success-icon"><Check size={15} /></span> Conversão concluída</div>
                <span className="section-label">Você recebe</span>
                <strong className="converted-value">{formatCurrency(result.valorConvertido, result.moedaDestino)}</strong>
                <div className="result-divider" />
                <div className="summary-line"><span>{formatCurrency(result.valorOriginal, result.moedaOrigem)}</span><ArrowRight size={16} /><span>{formatCurrency(result.valorConvertido, result.moedaDestino)}</span></div>
                <dl className="details">
                  <div><dt>1 USD equivale a</dt><dd>{formatCurrency(result.cotacaoDolar, 'BRL')}</dd></div>
                  <div><dt>Data da cotação</dt><dd>{formatDate(result.dataCotacao)}</dd></div>
                  <div><dt>Consultado em</dt><dd>{formatTime(result.consultadoEm)}</dd></div>
                </dl>
                <button className="new-conversion" type="button" onClick={() => setResult(null)}><RefreshCw size={16} /> Nova conversão</button>
              </div>
            )}
          </section>
        </div>

        {history.length > 0 && (
          <section className="history-section" aria-labelledby="history-title">
            <div className="history-heading">
              <div>
                <span className="section-label">Memória da sessão</span>
                <h2 id="history-title">Últimas cotações</h2>
              </div>
              <span className="history-count">{history.length}/5</span>
            </div>
            <div className="history-list">
              {history.map((conversion, index) => (
                <article className="history-item" key={`${conversion.consultadoEm}-${index}`}>
                  <div className="history-date">
                    <span>{formatDate(conversion.dataCotacao)}</span>
                    <small>{formatTime(conversion.consultadoEm)}</small>
                  </div>
                  <div className="history-values">
                    <strong>{formatCurrency(conversion.valorOriginal, conversion.moedaOrigem)}</strong>
                    <ArrowRight size={15} />
                    <strong>{formatCurrency(conversion.valorConvertido, conversion.moedaDestino)}</strong>
                  </div>
                  <span className="history-rate">1 USD = {formatCurrency(conversion.cotacaoDolar, 'BRL')}</span>
                </article>
              ))}
            </div>
          </section>
        )}
      </section>
      <footer>Dados consultados pela AwesomeAPI <span>•</span> USD / BRL</footer>
    </main>
  )
}

export default App
