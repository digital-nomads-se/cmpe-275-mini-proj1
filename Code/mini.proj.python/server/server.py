import logging
import socket
from concurrent.futures import ThreadPoolExecutor
import threading


logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

class Server:
    def __init__(self, host='localhost', port=2000, max_workers=10):
        self.host = host
        self.port = port
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)  
        self.server_socket.bind((self.host, self.port))
        self.thread_pool = ThreadPoolExecutor(max_workers=max_workers)
        self.sessions = {}  
        self.sessions_lock = threading.Lock()  
        self.shutdown_flag = threading.Event()
        logging.info("Server initialized and ready to start.")

    def handle_client(self, client_socket, address):
        logging.info(f"New client connection from {address}")
        session_id = str(address)  
        with self.sessions_lock:
            if session_id not in self.sessions:
                self.sessions[session_id] = {"messages": []}
            session = self.sessions[session_id]

        buffer_size = 1024  
        delimiter = '\n'  
        buffer = ''
        try:
            while True:
                data = client_socket.recv(buffer_size).decode('utf-8')
                if not data:
                    break  
                buffer += data
                while delimiter in buffer:
                    message, buffer = buffer.split(delimiter, 1)
                    logging.info(f"Received: {message} from {address}")
                    
                    client_socket.sendall((message + delimiter).encode('utf-8'))
                    with self.sessions_lock:
                        session["messages"].append(message)
        except ConnectionResetError:
            logging.error(f"Connection with {address} was reset by the client.")
        except Exception as e:
            logging.error(f"Error handling client {address}: {e}")
        finally:
            
            with self.sessions_lock:
                del self.sessions[session_id]
            client_socket.close()
            logging.info(f"Connection with {address} closed.")
        
    def wait_for_shutdown(self):
        input()  
        self.shutdown_flag.set()

    def start_server(self):
        self.server_socket.listen()
        logging.info(f"Server listening on {self.host}:{self.port}")
        self.server_socket.settimeout(1)  
        threading.Thread(target=self.wait_for_shutdown, daemon=True).start()  #
        try:
            while not self.shutdown_flag.is_set():  
                try:
                    client_socket, address = self.server_socket.accept()
                    self.thread_pool.submit(self.handle_client, client_socket, address)
                except socket.timeout:
                    continue  
        except Exception as e:
            logging.error(f"Error in server loop: {e}")
        finally:
            logging.info("Shutting down the server...")
            self.thread_pool.shutdown(wait=True)
            self.server_socket.close()
            logging.info("Server shutdown complete.")

if __name__ == "__main__":
    server = Server()
    server.start_server()


