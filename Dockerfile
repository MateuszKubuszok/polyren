FROM ghcr.io/graalvm/graalvm-ce:21.3.0

RUN gu available \
 && gu install native-image \
 && gu install python \
 && gu install ruby \
 && ./opt/graalvm-ce-java17-21.3.0/languages/ruby/lib/truffle/post_install_hook.sh \
 && gu rebuild-images --verbose polyglot ruby python

RUN gem install rouge \
 && mkdir /tmp/venv \
 && graalpython -m venv /tmp/venv \
 && source /tmp/venv/bin/activate \
 && pip install Pygments

CMD [ "bash" ]
