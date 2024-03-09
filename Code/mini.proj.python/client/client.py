import logging
import socket
import threading

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

class Client:
    def __init__(self, host='localhost', port=2000):
        self.host = host
        self.port = port

    
        self.running = True  

        self.initialize_socket()
        logging.info("Client initialized and attempting to connect to server.")

    def initialize_socket(self):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect((self.host, self.port))
        logging.info("Client connected to the server.")

    def receive_messages(self):
        while self.running:
            try:
                data = self.sock.recv(1024).decode('utf-8')
                if data:
                    logging.info(f"\nServer response: {data}")
            except OSError as e:
                logging.error(f"\nError receiving message: {e}")
                break

    def send_message(self, message):
        try:
            message += '\n'  
            encoded_message = message.encode('utf-8')
            self.sock.sendall(encoded_message)
            logging.info("Message sent to server.")
        except Exception as e:
            logging.error(f"\nError sending message: {e}")
            self.sock.close()

    def run(self):
        try:
            while True:
                message = input("Enter message to send (type 'quit' to exit): ")
                if message.lower() == 'quit':
                    self.quit()
                    break
                self.send_message(message)
        except KeyboardInterrupt:
            print("\nInterrupted by user")
            self.quit()

    def quit(self):
        self.send_message("quit")  
        self.running = False 
        self.sock.close()
        logging.info("Client closing connection.")

if __name__ == "__main__":
    client = Client()
    client.run()


