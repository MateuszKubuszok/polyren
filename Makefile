base-image:
	docker build --platform linux/amd64 --tag ghcr.io/mateuszkubuszok/polyren:base .

base-image-push:
	docker push ghcr.io/mateuszkubuszok/polyren:base .
