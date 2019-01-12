#!/bin/sh
#
# This is the entrypoint for the docker image.
#
set -e

PATH="/"
if [ -n "$BASE_HREF" ]; then
    PATH=$BASE_HREF
fi

echo "Set base-href to $PATH"
/bin/sed -i -e "s~<base href=\".*\">~<base href=\"$PATH\">~g" /usr/share/nginx/html/index.html

exec "$@"