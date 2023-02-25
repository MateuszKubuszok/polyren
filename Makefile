base-image:
	docker build --platform linux/amd64 --tag mateuszkubuszok/polyren-base:latest .

setup-graalvm:
	echo "Setup GraalVM"
	gu install native-image
	gu install python
	gu install ruby
	$JAVA_HOME/languages/ruby/lib/truffle/post_install_hook.sh
	gu rebuild-images --verbose polyglot ruby python
	echo "Setup Python dependencies"
	mkdir -p .venv
	graalpython -m venv .venv
	source /tmp/venv/bin/activate
	pip install Pygments
	echo "Setup Ruby dependencies"
	gem install rouge
