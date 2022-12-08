apt update
apt install -y zsh lsof protobuf-compiler
ln -s /usr/bin/protoc /usr/local/bin/protoc
sh -c "$(curl -fsSL https://raw.github.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"