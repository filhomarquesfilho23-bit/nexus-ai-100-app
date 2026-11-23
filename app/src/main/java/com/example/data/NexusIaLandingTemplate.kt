package com.example.data

object NexusIaLandingTemplate {

    const val DEFAULT_WHATSAPP_NUMBER = "5599999999999"

    fun buildHtml(whatsappNumber: String = DEFAULT_WHATSAPP_NUMBER): String {
        val cleanPhone = whatsappNumber.filter { it.isDigit() }.ifEmpty { DEFAULT_WHATSAPP_NUMBER }
        return """
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Nexus IA — Sites de Alta Conversão para sua Empresa</title>
  
  <!-- Ícones do Google Material e Fontes -->
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" />
  <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;600;700;800&display=swap" rel="stylesheet">

  <style>
    :root {
      --bg-dark: #030712;
      --card-bg: rgba(255, 255, 255, 0.02);
      --border-color: rgba(0, 210, 255, 0.15);
      --primary: #00d2ff;
      --secondary: #00ff88;
      --text: #ffffff;
      --text-muted: #94a3b8;
    }

    * {
      box-sizing: border-box;
      margin: 0;
      padding: 0;
      font-family: 'Plus Jakarta Sans', sans-serif;
    }

    body {
      background-color: var(--bg-dark);
      color: var(--text);
      display: flex;
      flex-direction: column;
      align-items: center;
      min-height: 100vh;
      padding: 16px;
      overflow-x: hidden;
    }

    .container {
      max-width: 480px;
      width: 100%;
      background: rgba(255, 255, 255, 0.01);
      border: 1px solid var(--border-color);
      border-radius: 28px;
      box-shadow: 0 15px 40px rgba(0, 0, 0, 0.6);
      padding: 24px 16px;
      margin-bottom: 24px;
    }

    /* Cabeçalho */
    header {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-bottom: 32px;
      text-align: center;
    }

    .logo-badge {
      display: flex;
      align-items: center;
      gap: 6px;
      background: rgba(0, 210, 255, 0.1);
      border: 1px solid rgba(0, 210, 255, 0.2);
      padding: 6px 14px;
      border-radius: 50px;
      font-size: 12px;
      font-weight: 700;
      color: var(--primary);
      text-transform: uppercase;
      letter-spacing: 1px;
      margin-bottom: 16px;
    }

    h1 {
      font-size: 26px;
      font-weight: 800;
      line-height: 1.2;
      margin-bottom: 12px;
      background: linear-gradient(135deg, #ffffff 30%, #00d2ff 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }

    p.subtitle {
      font-size: 14px;
      color: var(--text-muted);
      line-height: 1.5;
      max-width: 90%;
    }

    /* Seções Gerais */
    section {
      margin-bottom: 32px;
    }

    .section-title {
      font-size: 15px;
      font-weight: 700;
      color: var(--primary);
      text-transform: uppercase;
      letter-spacing: 1px;
      margin-bottom: 16px;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    /* Serviços Card */
    .service-card {
      background: var(--card-bg);
      border: 1px solid rgba(255, 255, 255, 0.03);
      border-radius: 20px;
      padding: 16px;
      margin-bottom: 12px;
      display: flex;
      gap: 14px;
      align-items: flex-start;
      transition: border 0.3s ease;
    }

    .service-card:hover {
      border: 1px solid rgba(0, 210, 255, 0.2);
    }

    .icon-box {
      width: 42px;
      height: 42px;
      background: rgba(0, 210, 255, 0.08);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--primary);
      flex-shrink: 0;
    }

    .service-info h3 {
      font-size: 14px;
      font-weight: 700;
      margin-bottom: 4px;
    }

    .service-info p {
      font-size: 12px;
      color: var(--text-muted);
      line-height: 1.4;
    }

    /* Como Funciona */
    .steps {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 10px;
    }

    .step-card {
      background: var(--card-bg);
      border: 1px solid rgba(255, 255, 255, 0.03);
      border-radius: 16px;
      padding: 12px;
      text-align: center;
    }

    .step-num {
      font-size: 20px;
      font-weight: 800;
      color: var(--secondary);
      margin-bottom: 4px;
    }

    .step-card h4 {
      font-size: 11px;
      font-weight: 700;
      margin-bottom: 2px;
    }

    .step-card p {
      font-size: 9px;
      color: var(--text-muted);
      line-height: 1.3;
    }

    /* Depoimento */
    .testimonial {
      background: rgba(0, 255, 136, 0.03);
      border: 1px solid rgba(0, 255, 136, 0.08);
      border-radius: 20px;
      padding: 16px;
      text-align: left;
    }

    .testimonial p {
      font-size: 13px;
      font-style: italic;
      color: var(--text);
      line-height: 1.5;
      margin-bottom: 12px;
    }

    .client {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .client-avatar {
      width: 28px;
      height: 28px;
      background: #334155;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: bold;
      font-size: 10px;
    }

    .client-info h4 {
      font-size: 11px;
      font-weight: 600;
    }

    /* Formulário do WhatsApp */
    .form-box {
      background: var(--card-bg);
      border: 1px solid var(--border-color);
      border-radius: 24px;
      padding: 20px 16px;
    }

    .form-box h2 {
      font-size: 18px;
      font-weight: 700;
      margin-bottom: 6px;
    }

    .form-box p {
      font-size: 12px;
      color: var(--text-muted);
      margin-bottom: 18px;
    }

    .input-group {
      margin-bottom: 12px;
      text-align: left;
    }

    .input-group label {
      display: block;
      font-size: 11px;
      font-weight: 600;
      color: var(--text-muted);
      margin-bottom: 4px;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .input-group input, .input-group textarea, .input-group select {
      width: 100%;
      background: rgba(0, 0, 0, 0.4);
      border: 1px solid rgba(255, 255, 255, 0.08);
      border-radius: 12px;
      padding: 12px;
      color: #fff;
      font-size: 14px;
      transition: border 0.3s ease;
    }

    .input-group input:focus, .input-group textarea:focus, .input-group select:focus {
      outline: none;
      border-color: var(--primary);
    }

    .btn-submit {
      width: 100%;
      background: linear-gradient(135deg, #00ff88 0%, #00b359 100%);
      color: #030712;
      border: none;
      border-radius: 14px;
      padding: 14px;
      font-size: 14px;
      font-weight: 800;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      margin-top: 16px;
      transition: transform 0.15s ease;
    }

    .btn-submit:active {
      transform: scale(0.97);
    }

    footer {
      font-size: 11px;
      color: var(--text-muted);
      text-align: center;
      margin-top: 16px;
    }
  </style>
</head>
<body>

  <div class="container">
    
    <!-- Cabeçalho Principal -->
    <header>
      <div class="logo-badge">
        <span class="material-symbols-outlined" style="font-size: 14px;">auto_awesome</span>
        <span>Sites Profissionais</span>
      </div>
      <h1>Tenha um Site que Vende Todos os Dias</h1>
      <p class="subtitle">Criamos sites modernos, ultra-rápidos e sob medida para alavancar as vendas da sua empresa.</p>
    </header>

    <!-- Seção: Nossos Serviços -->
    <section>
      <div class="section-title">
        <span class="material-symbols-outlined" style="font-size: 18px;">dashboard</span>
        <span>O que criamos</span>
      </div>

      <div class="service-card">
        <div class="icon-box">
          <span class="material-symbols-outlined">responsive_layout</span>
        </div>
        <div class="service-info">
          <h3>Landing Pages de Vendas</h3>
          <p>Páginas com foco total em converter visitantes em clientes. Ideais para quem faz anúncios.</p>
        </div>
      </div>

      <div class="service-card">
        <div class="icon-box">
          <span class="material-symbols-outlined">storefront</span>
        </div>
        <div class="service-info">
          <h3>Catálogos Digitais</h3>
          <p>Exiba e venda seus produtos sem complicações, com botões integrados ao WhatsApp.</p>
        </div>
      </div>

      <div class="service-card">
        <div class="icon-box">
          <span class="material-symbols-outlined">domain</span>
        </div>
        <div class="service-info">
          <h3>Sites Institucionais</h3>
          <p>Mais credibilidade para seu negócio com uma presença online profissional, robusta e confiável.</p>
        </div>
      </div>
    </section>

    <!-- Seção: Como Funciona -->
    <section>
      <div class="section-title">
        <span class="material-symbols-outlined" style="font-size: 18px;">task_alt</span>
        <span>Como Funciona</span>
      </div>
      <div class="steps">
        <div class="step-card">
          <div class="step-num">01</div>
          <h4>Envie a Ideia</h4>
          <p>Solicite seu orçamento agora.</p>
        </div>
        <div class="step-card">
          <div class="step-num">02</div>
          <h4>Criação</h4>
          <p>Desenhamos e colocamos no ar.</p>
        </div>
        <div class="step-card">
          <div class="step-num">03</div>
          <h4>Resultado</h4>
          <p>Seu negócio faturando na web.</p>
        </div>
      </div>
    </section>

    <!-- Seção: Prova Social -->
    <section>
      <div class="testimonial">
        <p>"Tivemos um aumento enorme de contatos de clientes no WhatsApp no primeiro mês que o site foi publicado."</p>
        <div class="client">
          <div class="client-avatar">PR</div>
          <div class="client-info">
            <h4>Pedro Rocha</h4>
            <p style="font-size: 10px; color: var(--text-muted);">Empresário local</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Seção: Formulário WhatsApp -->
    <section class="form-box">
      <h2>Solicitar Orçamento Grátis</h2>
      <p>Preencha os dados e receba uma proposta exclusiva via WhatsApp.</p>
      
      <div class="input-group">
        <label for="name">Seu Nome</label>
        <input type="text" id="name" placeholder="Ex: João Silva">
      </div>

      <div class="input-group">
        <label for="company">Nome da Empresa</label>
        <input type="text" id="company" placeholder="Ex: Padaria Silva">
      </div>

      <div class="input-group">
        <label for="serviceType">Tipo de Site Desejado</label>
        <select id="serviceType">
          <option value="Landing Page">Landing Page (Mais rápido)</option>
          <option value="Site Institucional">Site Institucional</option>
          <option value="Catálogo Virtual">Catálogo Virtual / E-commerce</option>
          <option value="Outro">Outro serviço</option>
        </select>
      </div>

      <div class="input-group">
        <label for="description">Fale brevemente do seu negócio</label>
        <textarea id="description" rows="3" placeholder="Ex: Preciso de uma página para vender meus serviços de..."></textarea>
      </div>

      <button class="btn-submit" onclick="enviarWhatsApp()">
        <span class="material-symbols-outlined">send</span>
        Enviar para o WhatsApp
      </button>
    </section>

    <footer>
      © Nexus IA — Todos os direitos reservados.
    </footer>

  </div>

  <script>
    function enviarWhatsApp() {
      const meuNumero = "$cleanPhone"; 

      const nome = document.getElementById("name").value.trim();
      const empresa = document.getElementById("company").value.trim();
      const servico = document.getElementById("serviceType").value;
      const descricao = document.getElementById("description").value.trim();

      if (!nome || !empresa) {
        alert("Por favor, preencha pelo menos o seu nome e o da sua empresa!");
        return;
      }

      const mensagem = "Olá! Vim através do site e gostaria de solicitar um orçamento para:\n\n" +
                       "👤 *Nome:* " + nome + "\n" +
                       "🏢 *Empresa:* " + empresa + "\n" +
                       "🌐 *Tipo de Site:* " + servico + "\n" +
                       "📝 *Sobre o negócio:* " + descricao;

      const linkZap = "https://api.whatsapp.com/send?phone=" + meuNumero + "&text=" + encodeURIComponent(mensagem);
      
      window.open(linkZap, "_blank");
    }
  </script>

</body>
</html>
""".trimIndent()
    }
}
