#!/bin/sh
#
# This is the entrypoint for the docker image.
#
set -e

if [ -n "$BASE_HREF" ]; then
    echo "Set base-href to $BASE_HREF"
    /bin/sed -i -e "s~<base href=\".*\">~<base href=\"$BASE_HREF\">~g" /usr/share/nginx/html/index.html
fi

exec "$@"