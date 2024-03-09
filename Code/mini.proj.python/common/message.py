class Message:
    @staticmethod
    def encode(message_str, server_name="Server", group_name="DefaultGroup"):
        """
        Encodes a message with server and group information.
        """
        return f"{server_name}:{group_name}:{message_str}".encode('utf-8')

    @staticmethod
    def decode(encoded_message):
        """
        Decodes a message into its components.
        """
        decoded_message = encoded_message.decode('utf-8')
        server_name, group_name, message_str = decoded_message.split(":", 2)
        return server_name, group_name, message_str