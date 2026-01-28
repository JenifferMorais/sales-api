#!/bin/sh
set -eu

info() {
  echo "[entrypoint] $*"
}

if [ -z "${QUARKUS_DATASOURCE_JDBC_URL:-}" ]; then
  if [ -n "${DATABASE_URL:-}" ]; then
    # Render "connectionString" format: postgresql://user:pass@host:port/db
    url_no_scheme="${DATABASE_URL#postgresql://}"
    url_no_scheme="${url_no_scheme#postgres://}"

    creds=""
    hostdb="$url_no_scheme"

    case "$url_no_scheme" in
      *@*)
        creds="${url_no_scheme%@*}"
        hostdb="${url_no_scheme#*@}"
        ;;
    esac

    user=""
    pass=""
    if [ -n "$creds" ]; then
      user="${creds%%:*}"
      pass="${creds#*:}"
      if [ "$pass" = "$creds" ]; then
        pass=""
      fi
    fi

    hostport="${hostdb%%/*}"
    dbpath="${hostdb#*/}"
    db="${dbpath%%\?*}"

    host="${hostport%%:*}"
    port="${hostport#*:}"
    if [ "$port" = "$hostport" ]; then
      port="5432"
    fi

    export QUARKUS_DATASOURCE_JDBC_URL="jdbc:postgresql://${host}:${port}/${db}"

    if [ -z "${QUARKUS_DATASOURCE_USERNAME:-}" ] && [ -n "$user" ]; then
      export QUARKUS_DATASOURCE_USERNAME="$user"
    fi
    if [ -z "${QUARKUS_DATASOURCE_PASSWORD:-}" ] && [ -n "$pass" ]; then
      export QUARKUS_DATASOURCE_PASSWORD="$pass"
    fi

    info "Datasource configurado via DATABASE_URL (host=${host} port=${port} db=${db} user=${QUARKUS_DATASOURCE_USERNAME:-})"
  elif [ -n "${DB_HOST:-}" ] && [ -n "${DB_NAME:-}" ]; then
    port="${DB_PORT:-5432}"
    export QUARKUS_DATASOURCE_JDBC_URL="jdbc:postgresql://${DB_HOST}:${port}/${DB_NAME}"
    if [ -n "${DB_USERNAME:-}" ] && [ -z "${QUARKUS_DATASOURCE_USERNAME:-}" ]; then
      export QUARKUS_DATASOURCE_USERNAME="${DB_USERNAME}"
    fi
    if [ -n "${DB_PASSWORD:-}" ] && [ -z "${QUARKUS_DATASOURCE_PASSWORD:-}" ]; then
      export QUARKUS_DATASOURCE_PASSWORD="${DB_PASSWORD}"
    fi
    info "Datasource configurado via DB_HOST/DB_NAME (host=${DB_HOST} port=${port} db=${DB_NAME} user=${QUARKUS_DATASOURCE_USERNAME:-})"
  else
    info "Nenhuma configuração de banco encontrada (defina DATABASE_URL ou QUARKUS_DATASOURCE_JDBC_URL)."
  fi
fi

JAVA_OPTS_DEFAULT="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
JAVA_OPTS="${JAVA_OPTS:-$JAVA_OPTS_DEFAULT}"
JAVA_APP_JAR="${JAVA_APP_JAR:-quarkus-run.jar}"

exec sh -c "java $JAVA_OPTS -jar $JAVA_APP_JAR"
