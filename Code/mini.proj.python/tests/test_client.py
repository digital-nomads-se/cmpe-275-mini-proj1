import unittest
from unittest.mock import patch, MagicMock
from client.client import Client  

class TestClient(unittest.TestCase):
    @patch('client.client.socket.socket')  
    def test_send_message(self, mock_socket):
        
        mock_socket_instance = MagicMock()
        mock_socket.return_value = mock_socket_instance

        client = Client(host='localhost', port=12345)
        client.send_message("Hello, server!")

        mock_socket_instance.sendall.assert_called_with(b"Hello, server!\n")

if __name__ == '__main__':
    unittest.main()



