import unittest
from unittest.mock import patch, MagicMock
from server.server import Server

class TestServer(unittest.TestCase):
    @patch('server.server.socket.socket')
    def test_server_startup(self, mock_socket):
       
        mock_client_socket = MagicMock()

        mock_socket.return_value.accept.return_value = (mock_client_socket, ('127.0.0.1', 12345))

        server = Server(host='localhost', port=12345, max_workers=2)
        with patch('server.server.ThreadPoolExecutor') as mock_executor:
            server.start_server()
            mock_socket.return_value.bind.assert_called_with(('localhost', 12345))
            mock_socket.return_value.listen.assert_called()
            mock_socket.return_value.accept.assert_called()

if __name__ == '__main__':
    unittest.main()

