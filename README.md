# Mais Paulista+ : Aplicativo Mobile da Prefeitura de Paulista - Pernambuco

Bem-vindo ao repositório do aplicativo móvel oficial da **Prefeitura de Paulista, Pernambuco**. Este aplicativo foi desenvolvido para facilitar o acesso dos cidadãos aos serviços municipais, notícias e informações relevantes sobre a cidade. Com uma interface intuitiva e funcionalidades práticas, o aplicativo visa melhorar a comunicação entre a prefeitura e os moradores de Paulista.

## Funcionalidades Principais

### 1. **Notícias e Acesso ao Site da Cidade**
- **Notícias:** Fique por dentro das últimas notícias e eventos da cidade. O aplicativo oferece uma seção dedicada para notícias atualizadas diretamente da prefeitura.
- **Acesso ao Site:** Com apenas um toque, você pode acessar o site oficial da cidade para obter mais informações e serviços.

### 2. **Agendamento de Consultas**
- **Marcar Consultas:** Agende consultas médicas diretamente pelo aplicativo.
- **Acompanhar Consultas:** Acompanhe o status das suas consultas agendadas.
- **Cancelar Consultas:** Caso necessário, cancele suas consultas de forma rápida e fácil.

### 3. **Requisições e Acompanhamento**
- **Fazer Requisições:** Envie solicitações relacionadas a serviços municipais, como reparos, limpeza, iluminação pública, entre outros.
- **Acompanhamento de Solicitações:** Acompanhe o status das suas solicitações por meio de três categorias:
  - **Pendente:** A solicitação foi recebida e está em análise.
  - **Deferido:** A solicitação foi aprovada e está em processo de execução.
  - **Indeferido:** A solicitação foi reprovada ou não pode ser atendida.

### 4. **Perguntas Frequentes (FAQ)**
- **FAQ:** Acesse uma lista de perguntas frequentes para obter respostas rápidas sobre os serviços municipais e o funcionamento do aplicativo.

### 5. **Contatos da Cidade**
- **Contatos:** Encontre os contatos oficiais da prefeitura, incluindo telefones, e-mails e endereços de diferentes secretarias e departamentos.


## Telas

### Tela de Login

![image](https://github.com/user-attachments/assets/417f8260-cb6b-4898-88d2-7377f97804a6)

### Tela de Cadastro 
![image](https://github.com/user-attachments/assets/64cf809f-e0dd-4642-b402-16da5a702435)














## Dispositivos Utilizados

O aplicativo utiliza os seguintes dispositivos do smartphone para oferecer uma experiência completa:

1. **Internet e Acesso à Rede**  
   - Conexão com a internet para acesso a serviços online, como notícias, agendamentos e requisições.
   - Sincronização de dados em tempo real.

2. **Câmera**  
   - Permite ao usuário tirar fotos ou acessar a galeria para enviar imagens como parte de requisições (ex.: fotos de problemas na cidade).

3. **GPS**  
   - Busca automática da localização do usuário para serviços baseados em geolocalização.
   - Possibilidade de alterar ou redefinir a localização manualmente.

---

## Funcionalidades

### 1. **Cadastro e Login de Usuários**
   - Cadastro simples e seguro utilizando senha e e-mail.
   - Login rápido para acessar todas as funcionalidades do aplicativo.

### 2. **Busca por Localização**
   - Busca automática da localização do usuário via GPS.
   - Opção para alterar ou redefinir a localização manualmente clicando no ícone de localização.

### 3. **Notificações de Consultas**
   - Notificações automáticas avisando sobre consultas agendadas:
     - **5 dias antes** da consulta.
     - **2 dias antes** da consulta.
     - **1 dia antes** da consulta.

### 4. **Fotos e Acesso à Galeria**
   - Permite ao usuário tirar fotos ou acessar a galeria para enviar imagens como parte de requisições (ex.: fotos de buracos na rua, lixo acumulado, etc.).

### 5. **Pop-ups de Alertas**
   - Pop-ups para alertar o usuário sobre informações importantes que acontecem no aplicativo
   (ex.: erro de autenticação, consultas agendadas e canceladas, requisições finalizadas, etc.)

### 6. **Redirecionamento para Contatos**
   - Redirecionamento para sites e aplicativos de contatos, como:
     - **Telefone:** Ligar diretamente para a prefeitura.
     - **WhatsApp:** Enviar mensagens via WhatsApp.
     - **E-mail:** Enviar e-mails diretamente.

### 7. **Busca por Consultas**
   - Busca de consultas baseada na **especialidade médica**.
   - Filtro de consultas por **status** (ex.: agendada, cancelada, concluída).

### 8. **Visualização e Status de Requisições**
   - Visualização do status das requisições por **cores**:
     - **Pendente:** Amarelo.
     - **Deferido:** Verde.
     - **Indeferido:** Vermelho.
   - Detalhes completos da requisição, incluindo descrição, data de envio e status atual.



## Banco de Dados
No projeto foi utilizado os serviços de autenticação e o Firestore do Firebase para gerenciar os dados e a segurança dos nossos usuários. A escolha desses serviços trouxe diversos benefícios e simplificou significativamente o processo de desenvolvimento.

### Autenticação

O serviço de autenticação do Firebase nos permitiu implementar um sistema seguro e robusto para a gestão de usuários. Utilizamos autenticação por e-mail e senha. A integração foi facilitada pelas bibliotecas fornecidas pelo Firebase, permitindo que o processo de login e registro fosse implementado de forma rápida e eficiente.

### Firestore

Para o gerenciamento dos dados, optamos pelo Firestore devido à sua capacidade de escalabilidade e flexibilidade. O Firestore é um banco de dados NoSQL, que nos permitiu armazenar e sincronizar dados em tempo real.


## Permissões 

No desenvolvimento do nosso aplicativo mobile, diversas permissões são solicitadas para garantir o funcionamento adequado das funcionalidades oferecidas. Abaixo, detalho cada uma das permissões requisitadas e sua importância no contexto do nosso projeto.

### 1. INTERNET
**Descrição:** Permissão para acessar a Internet.

**Motivo:** Esta permissão é fundamental para que o aplicativo possa se conectar ao firestone, realizar atualizações de dados e fornecer conteúdo online em tempo real aos usuários.

### 2. ACCESS_FINE_LOCATION
**Descrição:** Permissão para acessar a localização precisa do dispositivo.

**Motivo:** Utilizada para fornecer funcionalidades baseadas em localização, como recomendações de lugares próximos e personalização de conteúdo com base na localização exata do usuário.

### 3. ACCESS_COARSE_LOCATION
**Descrição:** Permissão para acessar a localização aproximada do dispositivo.

**Motivo:** Similar à permissão de localização precisa, mas com menor precisão. É utilizada em casos onde a precisão exata não é necessária, economizando energia e respeitando a privacidade do usuário.

### 4. ACCESS_NETWORK_STATE
**Descrição:** Permissão para acessar o estado da rede.

**Motivo:** Esta permissão permite que o aplicativo verifique o status da conexão de rede do dispositivo (Wi-Fi, dados móveis) para otimizar o uso de dados e prevenir falhas de comunicação durante a ausência de conexão.

### 5. READ_EXTERNAL_STORAGE
**Descrição:** Permissão para ler dados do armazenamento externo do dispositivo.

**Motivo:** Necessária para que o aplicativo possa acessar arquivos e dados armazenados pelo usuário, como fotos, vídeos e documentos, proporcionando uma melhor experiência e mais recursos.

### 6. WRITE_EXTERNAL_STORAGE
**Descrição:** Permissão para escrever dados no armazenamento externo do dispositivo.

**Motivo:** Permite que o aplicativo salve arquivos, documentos e outras informações no armazenamento externo, dando ao usuário a capacidade de baixar e armazenar conteúdo.

### 7. CALL_PHONE
**Descrição:** Permissão para realizar chamadas telefônicas diretamente do aplicativo.

**Motivo:** Usada em funcionalidades que envolvem a necessidade de ligar para números de telefone redirecionando para o aplicativo.

### 8. READ_MEDIA_IMAGES
**Descrição:** Permissão para ler imagens da mídia do dispositivo.

**Motivo:** Essencial para que o aplicativo possa acessar e exibir imagens armazenadas no dispositivo, oferecendo funcionalidades como upload de fotos e personalização de perfil.

### 9. POST_NOTIFICATIONS
**Descrição:** Permissão para enviar notificações para o dispositivo do usuário.

**Motivo:** Permite que o aplicativo envie notificações push para informar o usuário sobre atualizações importantes, mensagens, alertas e outros eventos relevantes em tempo real.

### 10. CAMERA
**Descrição:** Permissão para acessar a câmera do dispositivo.

**Motivo:** Utilizada para funcionalidades que requerem captura de fotos ou vídeos, como digitalização de documentos, captura de imagens para perfis e registro de momentos importantes.

### 11. RECORD_AUDIO
**Descrição:** Permissão para gravar áudio através do microfone do dispositivo.

**Motivo:** Necessária para funcionalidades que envolvem gravação de voz, chamadas de áudio, reconhecimento de fala e interação com o usuário através de comandos de voz.


## Contribuições
