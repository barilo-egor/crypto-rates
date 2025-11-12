<h1>💱 Crypto Rates Service</h1>

<p>Небольшой сервис для получения курсов валютных пар.  
Возвращает данные по запросу <code>GET /crypto-rates/{pair}</code>.  
Swagger-документация доступна и настраивается через <code>config/config.yml</code>.</p>

<hr>

<h2>⚙️ Основное</h2>
<ul>
  <li>Обрабатывает запросы: <code>GET /crypto-rates/{pair}</code></li>
  <li>Swagger-документация включается/отключается в <code>config/config.yml</code></li>
  <li>Готов к развёртыванию в Docker-контейнере</li>
</ul>

<hr>

<h2>📁 Структура проекта</h2>
<ul>
  <li><strong>/docker</strong> — файлы для Docker:
    <ul>
      <li><code>Dockerfile</code> — сборка образа</li>
      <li><code>bash.sh</code> — вход в контейнер</li>
      <li><code>logs.sh</code> — просмотр логов</li>
      <li><code>restart.sh</code> — перезапуск контейнера</li>
    </ul>
  </li>
  <li><strong>gradle-wrapper</strong> — для сборки через Gradle</li>
  <li><strong>config/config.yml</strong> — обязательный конфигурационный файл</li>
  <li>Собранный JAR: <code>build/libs/crypto-rates.jar</code></li>
</ul>

<hr>

<h2>🚀 Сборка и запуск</h2>

<pre><code># Сборка проекта
./gradlew clean bootJar

# или (если Gradle установлен глобально)
gradle clean bootJar

# Запуск приложения
java -jar build/libs/crypto-rates.jar --spring.config.location=config/config.yml
</code></pre>

<hr>

<h2>🧩 Конфигурация (<code>config/config.yml</code>)</h2>

<pre><code>rate:
  cache:
    ttl-seconds: 60   # Время (в секундах), в течение которого курс хранится в кэше

logging:
  level:
    root: INFO
    org.springframework: INFO
    org.hibernate: INFO
    tgb.cryptoexchange: DEBUG  # уровень логирования приложения

springdoc:
  swagger-ui:
    enabled: true                # включить/выключить Swagger UI
    path: /docs/swagger-ui.html  # путь для Swagger UI
  api-docs:
    path: /v3/api-docs
</code></pre>

<p><em>💡 Убедись, что файл <code>config/config.yml</code> присутствует в том же пакете, где и jar архив — без него сервис не запустится.</em></p>
